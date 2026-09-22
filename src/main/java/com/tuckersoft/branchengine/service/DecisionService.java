package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.Decision;
import com.tuckersoft.branchengine.domain.Playthrough;
import com.tuckersoft.branchengine.domain.StoryNode;
import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.dto.DecisionRequest;
import com.tuckersoft.branchengine.repository.DecisionRepository;
import com.tuckersoft.branchengine.repository.PlaythroughRepository;
import com.tuckersoft.branchengine.repository.StoryNodeRepository;
import com.tuckersoft.branchengine.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DecisionService(DecisionRepository decisionRepository, PlaythroughRepository playthroughRepository, StoryNodeRepository nodeRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.decisionRepository = decisionRepository;
        this.playthroughRepository = playthroughRepository;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Object processDecision(DecisionRequest request, String userEmail, String simulateHeader) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playthrough playthrough = playthroughRepository.findAll().stream()
                .filter(p -> p.getPlayerTag().equals(request.getPlayerTag()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Playthrough not found"));

        if (!playthrough.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        if ("FINALIZADA".equals(playthrough.getStatus())) {
            throw new RuntimeException("Partida finalizada");
        }

        String rawInput = request.getRawInput();
        String normalized = Normalizer.normalize(rawInput, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();

        String branchType;
        if (!normalized.matches(".*[a-z].*")) {
            branchType = "ENTRADA_CORRUPTA";
        } else if (normalized.contains("netflix") || normalized.contains("camara") || normalized.contains("espectador") || normalized.contains("videojuego")) {
            branchType = "RUPTURA_CUARTA_PARED";
        } else if (normalized.contains("vigilan") || normalized.contains("simbolo") || normalized.contains("conspiracion")) {
            branchType = "SOSPECHA";
        } else if (normalized.contains("rechaza") || normalized.contains("destruye") || normalized.contains("desobedece") || normalized.contains("renuncia")) {
            branchType = "REBELDIA";
        } else {
            branchType = "OBEDIENCIA";
        }

        String handlerUnit;
        String outcomeCode;
        switch (branchType) {
            case "OBEDIENCIA": handlerUnit = "Mesa de Guion"; outcomeCode = "ADVANCE_MAIN_PATH"; break;
            case "REBELDIA": handlerUnit = "Control de Continuidad"; outcomeCode = "FORK_TIMELINE"; break;
            case "SOSPECHA": handlerUnit = "Oficina de Seguridad"; outcomeCode = "INJECT_WHITE_BEAR_SYMBOL"; break;
            case "RUPTURA_CUARTA_PARED": handlerUnit = "Departamento Netflix"; outcomeCode = "BREAK_FOURTH_WALL"; break;
            default: handlerUnit = "Archivo de Errores"; outcomeCode = "DISCARD_INPUT"; break;
        }

        Decision decision = new Decision();
        decision.setPlaythrough(playthrough);
        decision.setNode(playthrough.getCurrentNode());
        decision.setRawInput(rawInput);
        decision.setBranchType(branchType);
        decision.setImpactLevel(request.getImpactLevel());
        decision.setHandlerUnit(handlerUnit);
        decision.setOutcomeCode(outcomeCode);

        if ("ENTRADA_CORRUPTA".equals(branchType)) {
            decision.setResolvedNodeCode(null);
            decision.setStatus("ERROR");
            return decisionRepository.save(decision);
        }

        // Stats calculation
        int lucidityChange = 0;
        int controlChange = 0;
        switch (request.getImpactLevel()) {
            case "LEVE": lucidityChange = -5; controlChange = 5; break;
            case "MODERADO": lucidityChange = -15; controlChange = 10; break;
            case "GRAVE": lucidityChange = -30; controlChange = 20; break;
            case "CRITICO": lucidityChange = -40; controlChange = 45; break;
        }

        playthrough.setLucidity(Math.max(0, Math.min(100, playthrough.getLucidity() + lucidityChange)));
        playthrough.setControlLevel(Math.max(0, Math.min(100, playthrough.getControlLevel() + controlChange)));

        String nextNodeCode;
        if ("RUPTURA_CUARTA_PARED".equals(branchType) || "CRITICO".equals(request.getImpactLevel())) {
            nextNodeCode = playthrough.getCurrentNode().getGlitchBranchCode();
        } else {
            nextNodeCode = playthrough.getCurrentNode().getPrimaryBranchCode();
        }
        decision.setResolvedNodeCode(nextNodeCode);

        // Path resolution
        if (playthrough.getControlLevel() >= 100) {
            playthrough.setStatus("FINALIZADA");
            playthrough.setEndingCode("ENDING_PAC_SYMBOL");
        } else if (playthrough.getLucidity() <= 0) {
            playthrough.setStatus("FINALIZADA");
            playthrough.setEndingCode("ENDING_WHITE_BEAR");
        } else if (nextNodeCode == null || !nodeRepository.existsByNodeCode(nextNodeCode)) {
            playthrough.setStatus("FINALIZADA");
            playthrough.setEndingCode("ENDING_NETFLIX_CUT");
        } else {
            playthrough.setStatus("ACTIVA");
            StoryNode nextNode = nodeRepository.findByNodeCode(nextNodeCode).get();
            playthrough.setCurrentNode(nextNode);
        }

        playthroughRepository.save(playthrough);

        decision.setStatus("REGISTRADA");
        decisionRepository.save(decision);

        eventPublisher.publishEvent(new com.tuckersoft.branchengine.event.DecisionCommittedEvent(decision.getId(), simulateHeader));

        return decision; // or a DTO
    }
}
