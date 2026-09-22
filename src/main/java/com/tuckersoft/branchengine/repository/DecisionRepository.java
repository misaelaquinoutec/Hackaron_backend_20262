package com.tuckersoft.branchengine.repository;

import com.tuckersoft.branchengine.domain.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Decision> {
    List<Decision> findByPlaythroughIdOrderByIdAsc(Long playthroughId);
}
