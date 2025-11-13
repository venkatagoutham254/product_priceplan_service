package aforo.productrateplanservice.security.audit;

import aforo.productrateplanservice.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 📋 Security Audit Logging Service
 * Tracks security-related events and user actions
 */
@Service("securityAuditService")
@Slf4j
public class SecurityAuditService {

    /**
     * Log successful authorization
     */
    public void logAuthorizationSuccess(String action, String resource, Map<String, Object> details) {
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .eventType(AuditEventType.AUTHORIZATION_SUCCESS)
                .userId("unknown")
                .organizationId(TenantContext.get())
                .action(action)
                .resource(resource)
                .outcome(AuditOutcome.SUCCESS)
                .details(details)
                .build();
        
        logAuditEvent(event);
    }

    /**
     * Log failed authorization
     */
    public void logAuthorizationFailure(String action, String resource, String reason, Map<String, Object> details) {
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .eventType(AuditEventType.AUTHORIZATION_FAILURE)
                .userId("unknown")
                .organizationId(TenantContext.get())
                .action(action)
                .resource(resource)
                .outcome(AuditOutcome.FAILURE)
                .reason(reason)
                .details(details)
                .build();
        
        logAuditEvent(event);
    }

    /**
     * Log data access
     */
    public void logDataAccess(String action, String resource, Long resourceId, Map<String, Object> details) {
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .eventType(AuditEventType.DATA_ACCESS)
                .userId("unknown")
                .organizationId(TenantContext.get())
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .outcome(AuditOutcome.SUCCESS)
                .details(details)
                .build();
        
        logAuditEvent(event);
    }

    /**
     * Log data modification
     */
    public void logDataModification(String action, String resource, Long resourceId, 
                                   Object oldValue, Object newValue, Map<String, Object> details) {
        Map<String, Object> modificationDetails = new java.util.HashMap<>(details != null ? details : Map.of());
        modificationDetails.put("oldValue", oldValue);
        modificationDetails.put("newValue", newValue);
        
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .eventType(AuditEventType.DATA_MODIFICATION)
                .userId("unknown")
                .organizationId(TenantContext.get())
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .outcome(AuditOutcome.SUCCESS)
                .details(modificationDetails)
                .build();
        
        logAuditEvent(event);
    }

    /**
     * Log security event
     */
    public void logSecurityEvent(AuditEventType eventType, String description, Map<String, Object> details) {
        AuditEvent event = AuditEvent.builder()
                .timestamp(LocalDateTime.now())
                .eventType(eventType)
                .userId("unknown")
                .organizationId(TenantContext.get())
                .action(eventType.name())
                .resource("SECURITY")
                .outcome(AuditOutcome.SUCCESS)
                .reason(description)
                .details(details)
                .build();
        
        logAuditEvent(event);
    }

    /**
     * Central audit event logging
     */
    private void logAuditEvent(AuditEvent event) {
        // In a production system, this would:
        // 1. Store in audit database table
        // 2. Send to external SIEM system
        // 3. Trigger alerts for critical events
        
        // For now, log to application logs with structured format
        log.info("🔍 AUDIT: {} | User: {} | Org: {} | Action: {} | Resource: {} | Outcome: {} | Details: {}", 
                event.getEventType(),
                event.getUserId(),
                event.getOrganizationId(),
                event.getAction(),
                event.getResource(),
                event.getOutcome(),
                event.getDetails());
        
        // Log security failures at WARN level
        if (event.getOutcome() == AuditOutcome.FAILURE) {
            log.warn("🚨 SECURITY FAILURE: {} - {} | User: {} | Org: {} | Reason: {}", 
                    event.getEventType(),
                    event.getAction(),
                    event.getUserId(),
                    event.getOrganizationId(),
                    event.getReason());
        }
    }

    /**
     * Audit event data structure
     */
    public static class AuditEvent {
        private LocalDateTime timestamp;
        private AuditEventType eventType;
        private String userId;
        private Long organizationId;
        private String action;
        private String resource;
        private Long resourceId;
        private AuditOutcome outcome;
        private String reason;
        private Map<String, Object> details;

        // Builder pattern
        public static AuditEventBuilder builder() {
            return new AuditEventBuilder();
        }

        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public AuditEventType getEventType() { return eventType; }
        public String getUserId() { return userId; }
        public Long getOrganizationId() { return organizationId; }
        public String getAction() { return action; }
        public String getResource() { return resource; }
        public Long getResourceId() { return resourceId; }
        public AuditOutcome getOutcome() { return outcome; }
        public String getReason() { return reason; }
        public Map<String, Object> getDetails() { return details; }

        // Builder class
        public static class AuditEventBuilder {
            private final AuditEvent event = new AuditEvent();

            public AuditEventBuilder timestamp(LocalDateTime timestamp) {
                event.timestamp = timestamp;
                return this;
            }

            public AuditEventBuilder eventType(AuditEventType eventType) {
                event.eventType = eventType;
                return this;
            }

            public AuditEventBuilder userId(String userId) {
                event.userId = userId;
                return this;
            }

            public AuditEventBuilder organizationId(Long organizationId) {
                event.organizationId = organizationId;
                return this;
            }

            public AuditEventBuilder action(String action) {
                event.action = action;
                return this;
            }

            public AuditEventBuilder resource(String resource) {
                event.resource = resource;
                return this;
            }

            public AuditEventBuilder resourceId(Long resourceId) {
                event.resourceId = resourceId;
                return this;
            }

            public AuditEventBuilder outcome(AuditOutcome outcome) {
                event.outcome = outcome;
                return this;
            }

            public AuditEventBuilder reason(String reason) {
                event.reason = reason;
                return this;
            }

            public AuditEventBuilder details(Map<String, Object> details) {
                event.details = details;
                return this;
            }

            public AuditEvent build() {
                return event;
            }
        }
    }

    /**
     * Audit event types
     */
    public enum AuditEventType {
        AUTHORIZATION_SUCCESS,
        AUTHORIZATION_FAILURE,
        DATA_ACCESS,
        DATA_MODIFICATION,
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        PERMISSION_GRANTED,
        PERMISSION_DENIED,
        SUSPICIOUS_ACTIVITY,
        SECURITY_VIOLATION
    }

    /**
     * Audit outcomes
     */
    public enum AuditOutcome {
        SUCCESS,
        FAILURE,
        WARNING
    }
}
