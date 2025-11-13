package aforo.productrateplanservice.security;

import aforo.productrateplanservice.config.RateLimitConfig;
import aforo.productrateplanservice.tenant.TenantContext;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitConfig rateLimitConfig;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateRateLimitKey(rateLimit.keyType());
        Bucket bucket = rateLimitConfig.createBucket(key, rateLimit.type());
        
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            log.debug("Rate limit check passed for key: {}, remaining tokens: {}", key, probe.getRemainingTokens());
            return joinPoint.proceed();
        } else {
            log.warn("Rate limit exceeded for key: {}, retry after: {} seconds", 
                    key, probe.getNanosToWaitForRefill() / 1_000_000_000);
            
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, 
                "Rate limit exceeded. Try again in " + (probe.getNanosToWaitForRefill() / 1_000_000_000) + " seconds"
            );
        }
    }

    private String generateRateLimitKey(RateLimitKeyType keyType) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        return switch (keyType) {
            case IP_ADDRESS -> "rate_limit:ip:" + getClientIpAddress(request);
            case TENANT -> "rate_limit:tenant:" + TenantContext.require();
            case USER_AGENT -> "rate_limit:ua:" + request.getHeader("User-Agent");
            case COMBINED -> "rate_limit:combined:" + TenantContext.require() + ":" + getClientIpAddress(request);
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RateLimit {
        RateLimitConfig.RateLimitType type() default RateLimitConfig.RateLimitType.GENERAL_API;
        RateLimitKeyType keyType() default RateLimitKeyType.TENANT;
    }

    public enum RateLimitKeyType {
        IP_ADDRESS,
        TENANT,
        USER_AGENT,
        COMBINED
    }
}
