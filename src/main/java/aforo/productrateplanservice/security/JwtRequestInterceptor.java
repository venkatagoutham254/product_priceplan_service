package aforo.productrateplanservice.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import aforo.productrateplanservice.tenant.TenantContext;


@Component
public class JwtRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            TenantContext.setJwt(authHeader.substring(7)); // remove "Bearer "
        }
        return true;
    }
}
