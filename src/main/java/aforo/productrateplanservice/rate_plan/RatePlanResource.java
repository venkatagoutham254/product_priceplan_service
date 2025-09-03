package aforo.productrateplanservice.rate_plan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import aforo.productrateplanservice.product.dto.ProductDTO;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/rateplans")
@RequiredArgsConstructor
@Tag(name = "Rate Plans", description = "Create, update, confirm and list rate plans")
public class RatePlanResource {

    private final RatePlanService ratePlanService;

    @PostMapping
    @Operation(summary = "Create rate plan")
    public ResponseEntity<RatePlanDTO> createRatePlan(@RequestBody CreateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.createRatePlan(request));
    }

    @GetMapping
    @Operation(summary = "List rate plans")
    public ResponseEntity<List<RatePlanDTO>> getAllRatePlans() {
        return ResponseEntity.ok(ratePlanService.getAllRatePlans());
    }

    @GetMapping("/{ratePlanId}")
    @Operation(summary = "Get rate plan by ID")
    public ResponseEntity<RatePlanDTO> getRatePlanById(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(ratePlanService.getRatePlanById(ratePlanId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "List rate plans by product ID")
    public ResponseEntity<List<RatePlanDTO>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ratePlanService.getRatePlansByProductId(productId));
    }

    @DeleteMapping("/{ratePlanId}")
    @Operation(summary = "Delete rate plan")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long ratePlanId) {
        ratePlanService.deleteRatePlan(ratePlanId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{ratePlanId}")
    @Operation(summary = "Update rate plan (full)")
    public ResponseEntity<RatePlanDTO> updateRatePlanFully(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanFully(ratePlanId, request));
    }
    
    @PatchMapping("/{ratePlanId}")
    @Operation(summary = "Update rate plan (partial)")
    public ResponseEntity<RatePlanDTO> updateRatePlanPartially(
            @PathVariable Long ratePlanId,
            @RequestBody UpdateRatePlanRequest request) {
        return ResponseEntity.ok(ratePlanService.updateRatePlanPartially(ratePlanId, request));
    }
    
    @PostMapping("/{ratePlanId}/confirm")
    @Operation(summary = "Confirm rate plan")
    public ResponseEntity<RatePlanDTO> confirmRatePlan(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(ratePlanService.confirmRatePlan(ratePlanId));
    }
    


    }
