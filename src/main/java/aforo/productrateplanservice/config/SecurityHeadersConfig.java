package aforo.productrateplanservice.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }

    private static class SecurityHeadersFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                throws IOException, ServletException {
            
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Security Headers
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            httpResponse.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
            
            // Content Security Policy
            httpResponse.setHeader("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self'; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none';"
            );
            
            // HSTS (HTTP Strict Transport Security)
            httpResponse.setHeader("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains; preload");
            
            // Remove server information
            httpResponse.setHeader("Server", "");
            
            chain.doFilter(request, response);
        }
    }
}
