package aforo.productrateplanservice.audit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AuditEvent {
    private String eventId;
    private String eventType;
    private String userId;
    private Long organizationId;
    private String action;
    private String resource;
    private String resourceId;
    private AuditResult result;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private Map<String, Object> details;
    private String correlationId;

    public enum AuditResult {
        SUCCESS,
        FAILURE,
        UNAUTHORIZED,
        FORBIDDEN
    }
}
