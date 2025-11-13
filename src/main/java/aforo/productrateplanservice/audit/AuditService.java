package aforo.productrateplanservice.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final ObjectMapper objectMapper;

    @Async("taskExecutor")
    public void logAuditEvent(AuditEvent event) {
        try {
            // Set timestamp and correlation ID if not already set
            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }

            // Log as structured JSON for compliance and monitoring
            String auditJson = objectMapper.writeValueAsString(event);
            log.info("AUDIT_EVENT: {}", auditJson);

            // In production, you might also:
            // 1. Send to external audit system (e.g., Splunk, ELK)
            // 2. Store in dedicated audit database
            // 3. Send to message queue for processing
            
        } catch (Exception e) {
            log.error("Failed to log audit event: {}", event, e);
        }
    }

    public AuditEvent.AuditEventBuilder createAuditEvent(String eventType, String action) {
        return AuditEvent.builder()
                .eventType(eventType)
                .action(action)
                .timestamp(LocalDateTime.now())
                .eventId(UUID.randomUUID().toString());
    }
}
