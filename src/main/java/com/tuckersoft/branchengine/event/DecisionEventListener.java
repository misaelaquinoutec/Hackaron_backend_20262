package com.tuckersoft.branchengine.event;

import com.tuckersoft.branchengine.domain.Decision;
import com.tuckersoft.branchengine.domain.RealityLog;
import com.tuckersoft.branchengine.repository.DecisionRepository;
import com.tuckersoft.branchengine.repository.RealityLogRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class DecisionEventListener {

    private final DecisionRepository decisionRepository;
    private final RealityLogRepository realityLogRepository;
    private final JavaMailSender mailSender;

    public DecisionEventListener(DecisionRepository decisionRepository, RealityLogRepository realityLogRepository, JavaMailSender mailSender) {
        this.decisionRepository = decisionRepository;
        this.realityLogRepository = realityLogRepository;
        this.mailSender = mailSender;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDecisionCommitted(DecisionCommittedEvent event) {
        Decision decision = decisionRepository.findById(event.getDecisionId()).orElseThrow();
        decision.setStatus("PROCESANDO");
        decisionRepository.save(decision);

        RealityLog log = new RealityLog();
        log.setDecision(decision);
        log.setRecipientEmail(decision.getPlaythrough().getUser().getEmail());
        String subject = String.format("[TUCKERSOFT] %s en %s | Impacto %s", 
            decision.getBranchType(), 
            decision.getPlaythrough().getPlayerTag(), 
            decision.getImpactLevel());
        log.setSubject(subject);

        try {
            if ("MAIL_FAILURE".equals(event.getSimulateHeader())) {
                throw new RuntimeException("Simulated mail failure");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(log.getRecipientEmail());
            message.setSubject(log.getSubject());
            
            String text = String.format(
                "Decision #%d\nPlayer: %s\nBranch: %s\nInput: %s\n",
                decision.getId(),
                decision.getPlaythrough().getPlayerTag(),
                decision.getBranchType(),
                decision.getRawInput()
            );
            message.setText(text);
            
            mailSender.send(message);

            log.setLogStatus("SENT");
            log.setSentAt(Instant.now());
            decision.setStatus("ESTABILIZADA");
        } catch (Exception e) {
            log.setLogStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            decision.setStatus("ERROR");
        }

        realityLogRepository.save(log);
        decisionRepository.save(decision);
    }
}
