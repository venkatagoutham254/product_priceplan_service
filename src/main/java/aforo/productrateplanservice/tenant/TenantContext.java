package aforo.productrateplanservice.tenant;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class TenantContext {

    private static final ThreadLocal<Long> ORG = new ThreadLocal<>();
    private static final ThreadLocal<String> JWT = new ThreadLocal<>();

    private TenantContext() {}

    // --- Organization ID handling ---
    public static void set(Long organizationId) {
        ORG.set(organizationId);
    }

    public static Long get() {
        return ORG.get();
    }

    public static Long require() {
        Long id = ORG.get();
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenant");
        }
        return id;
    }

    // --- JWT handling ---
    public static void setJwt(String token) {
        JWT.set(token);
    }

    public static String getJwt() {
        return JWT.get();
    }

    public static String requireJwt() {
        String token = JWT.get();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT token");
        }
        return token;
    }

    // --- Cleanup ---
    public static void clear() {
        ORG.remove();
        JWT.remove();
    }
}
