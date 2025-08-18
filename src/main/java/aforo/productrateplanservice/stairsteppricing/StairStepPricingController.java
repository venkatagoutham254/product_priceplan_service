package aforo.productrateplanservice.stairsteppricing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/stairstep")
@RequiredArgsConstructor
public class StairStepPricingController {

    private final StairStepPricingService stairStepPricingService;

    @PostMapping
    public ResponseEntity<StairStepPricingDTO> create(
            @PathVariable Long ratePlanId,
            @Valid @RequestBody StairStepPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(stairStepPricingService.create(ratePlanId, dto));
    }

    @PutMapping("/{stairStepPricingId}")
    public ResponseEntity<StairStepPricingDTO> update(
        @PathVariable Long ratePlanId,
        @PathVariable Long stairStepPricingId,
        @Valid @RequestBody StairStepPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(stairStepPricingService.update(ratePlanId, stairStepPricingId, dto));
    }

    @GetMapping
    public ResponseEntity<List<StairStepPricingDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(stairStepPricingService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{stairStepPricingId}")
    public ResponseEntity<StairStepPricingDTO> getById(@PathVariable Long stairStepPricingId) {
        return ResponseEntity.ok(stairStepPricingService.getById(stairStepPricingId));
    }

    @DeleteMapping("/{stairStepPricingId}")
    public ResponseEntity<Void> delete(@PathVariable Long stairStepPricingId) {
        stairStepPricingService.deleteById(stairStepPricingId);
        return ResponseEntity.noContent().build();
    }
}
