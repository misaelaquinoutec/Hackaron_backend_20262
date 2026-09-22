package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.Playthrough;
import com.tuckersoft.branchengine.domain.StoryNode;
import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.dto.PlaythroughDto;
import com.tuckersoft.branchengine.repository.PlaythroughRepository;
import com.tuckersoft.branchengine.repository.StoryNodeRepository;
import com.tuckersoft.branchengine.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaythroughService {

    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository nodeRepository;
    private final UserRepository userRepository;

    public PlaythroughService(PlaythroughRepository playthroughRepository, StoryNodeRepository nodeRepository, UserRepository userRepository) {
        this.playthroughRepository = playthroughRepository;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Playthrough createPlaythrough(String playerTag, String startNodeCode, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StoryNode node = nodeRepository.findByNodeCode(startNodeCode)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        if (playthroughRepository.existsByPlayerTag(playerTag)) {
            throw new RuntimeException("PlayerTag already in use");
        }

        if (node.getCurrentBranches() >= node.getBranchCapacity()) {
            throw new RuntimeException("Node capacity exceeded");
        }

        node.setCurrentBranches(node.getCurrentBranches() + 1);
        nodeRepository.save(node);

        Playthrough playthrough = new Playthrough();
        playthrough.setPlayerTag(playerTag);
        playthrough.setUser(user);
        playthrough.setStartNodeCode(node.getNodeCode());
        playthrough.setCurrentNode(node);
        playthrough.setLucidity(100);
        playthrough.setControlLevel(0);
        playthrough.setStatus("ACTIVA");

        return playthroughRepository.save(playthrough);
    }

    @Transactional(readOnly = true)
    public List<PlaythroughDto> getPlaythroughs(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ROLE_ADMIN".equals(user.getRole())) {
            return playthroughRepository.findAll().stream().map(PlaythroughDto::new).collect(Collectors.toList());
        } else {
            return playthroughRepository.findByUserId(user.getId()).stream().map(PlaythroughDto::new).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public PlaythroughDto getPlaythrough(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Playthrough p = playthroughRepository.findById(id).orElseThrow(() -> new RuntimeException("Playthrough not found"));

        if (!"ROLE_ADMIN".equals(user.getRole()) && !p.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }
        return new PlaythroughDto(p);
    }

    @Transactional(readOnly = true)
    public Object getPlaythroughPath(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Playthrough p = playthroughRepository.findById(id).orElseThrow(() -> new RuntimeException("Playthrough not found"));

        if (!"ROLE_ADMIN".equals(user.getRole()) && !p.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        // We need to return the path structure
        return java.util.Map.of(
            "playthroughId", p.getId(),
            "startNodeCode", p.getStartNodeCode(),
            "steps", List.of() // Need to fetch decisions!
        );
    }
}
