package aforo.productrateplanservice.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestResponseLoggingFilter implements Filter {

    private static final List<String> SENSITIVE_HEADERS = Arrays.asList(
        "authorization", "x-api-key", "cookie", "set-cookie"
    );
    
    private static final List<Pattern> SENSITIVE_PATTERNS = Arrays.asList(
        Pattern.compile("\"password\"\\s*:\\s*\"[^\"]*\"", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\"token\"\\s*:\\s*\"[^\"]*\"", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\"secret\"\\s*:\\s*\"[^\"]*\"", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\"apiKey\"\\s*:\\s*\"[^\"]*\"", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip logging for actuator endpoints and static resources
        String requestURI = httpRequest.getRequestURI();
        if (shouldSkipLogging(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String correlationId = UUID.randomUUID().toString();
        httpResponse.setHeader("X-Correlation-ID", correlationId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();
        
        try {
            logRequest(wrappedRequest, correlationId);
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedResponse, correlationId, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private boolean shouldSkipLogging(String requestURI) {
        return requestURI.startsWith("/actuator") || 
               requestURI.startsWith("/swagger") ||
               requestURI.startsWith("/v3/api-docs") ||
               requestURI.contains("/static/") ||
               requestURI.endsWith(".css") ||
               requestURI.endsWith(".js") ||
               requestURI.endsWith(".ico");
    }

    private void logRequest(ContentCachingRequestWrapper request, String correlationId) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String clientIp = getClientIpAddress(request);
            String userAgent = maskSensitiveData(request.getHeader("User-Agent"));
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("REQUEST [").append(correlationId).append("] ");
            logMessage.append(method).append(" ").append(uri);
            if (queryString != null) {
                logMessage.append("?").append(maskSensitiveData(queryString));
            }
            logMessage.append(" from ").append(clientIp);
            if (userAgent != null) {
                logMessage.append(" UA: ").append(userAgent);
            }

            // Log request body for POST/PUT/PATCH (with sensitive data masking)
            if (Arrays.asList("POST", "PUT", "PATCH").contains(method)) {
                String body = getRequestBody(request);
                if (body != null && !body.trim().isEmpty()) {
                    logMessage.append(" Body: ").append(maskSensitiveData(body));
                }
            }

            log.info(logMessage.toString());
            
        } catch (Exception e) {
            log.warn("Failed to log request: {}", e.getMessage());
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, String correlationId, long duration) {
        try {
            int status = response.getStatus();
            String responseBody = getResponseBody(response);
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("RESPONSE [").append(correlationId).append("] ");
            logMessage.append("Status: ").append(status);
            logMessage.append(" Duration: ").append(duration).append("ms");
            
            if (responseBody != null && !responseBody.trim().isEmpty() && status >= 400) {
                logMessage.append(" Body: ").append(maskSensitiveData(responseBody));
            }

            if (status >= 400) {
                log.warn(logMessage.toString());
            } else {
                log.info(logMessage.toString());
            }
            
        } catch (Exception e) {
            log.warn("Failed to log response: {}", e.getMessage());
        }
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

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }

    private String maskSensitiveData(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String masked = input;
        
        // Mask sensitive patterns in JSON
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            masked = pattern.matcher(masked).replaceAll("\"$1\":\"***MASKED***\"");
        }
        
        // HTML encode to prevent XSS in logs
        return Encode.forHtml(masked);
    }
}
