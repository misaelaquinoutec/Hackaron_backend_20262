package com.tuckersoft.branchengine.repository;

import com.tuckersoft.branchengine.domain.Playthrough;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaythroughRepository extends JpaRepository<Playthrough, Long> {
    boolean existsByPlayerTag(String playerTag);
    List<Playthrough> findByUserId(Long userId);
}
