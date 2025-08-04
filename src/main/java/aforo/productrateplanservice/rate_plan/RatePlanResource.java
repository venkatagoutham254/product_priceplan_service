package aforo.productrateplanservice.rate_plan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans")
@RequiredArgsConstructor
public class RatePlanResource {

    private final RatePlanService ratePlanService;

    @PostMapping
    public ResponseEntity<RatePlanDTO> createRatePlan(@RequestBody CreateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.createRatePlan(request));
    }

    @GetMapping
    public ResponseEntity<List<RatePlanDTO>> getAllRatePlans() {
        return ResponseEntity.ok(ratePlanService.getAllRatePlans());
    }

    @GetMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> getRatePlanById(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(ratePlanService.getRatePlanById(ratePlanId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RatePlanDTO>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ratePlanService.getRatePlansByProductId(productId));
    }

    @DeleteMapping("/{ratePlanId}")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long ratePlanId) {
        ratePlanService.deleteRatePlan(ratePlanId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> updateRatePlanFully(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanFully(ratePlanId, request));
    }
    
    @PatchMapping("/{ratePlanId}")
    public ResponseEntity<RatePlanDTO> updateRatePlanPartially(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanPartially(ratePlanId, request));
    }
    
    @PostMapping("/api/rateplans/{ratePlanId}/confirm")
    public ResponseEntity<Void> confirmRatePlan(@PathVariable Long ratePlanId) {
        ratePlanService.confirmRatePlan(ratePlanId);
        return ResponseEntity.ok().build();
    }
    

    }
