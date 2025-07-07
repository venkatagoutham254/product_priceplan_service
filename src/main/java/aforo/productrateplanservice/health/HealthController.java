package aforo.productrateplanservice.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple API health-check endpoint. Responds with `{ "status": "UP" }` so that
 * external load-balancers or CI/CD pipelines can verify the service has started.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health")
public class HealthController {

    @Operation(summary = "Returns service health status")
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
