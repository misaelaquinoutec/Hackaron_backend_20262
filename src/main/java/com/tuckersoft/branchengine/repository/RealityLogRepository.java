package com.tuckersoft.branchengine.repository;

import com.tuckersoft.branchengine.domain.RealityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealityLogRepository extends JpaRepository<RealityLog, Long> {
    java.util.List<RealityLog> findByDecisionId(Long decisionId);
}
