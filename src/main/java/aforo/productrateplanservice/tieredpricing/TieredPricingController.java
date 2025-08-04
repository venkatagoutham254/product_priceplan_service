package aforo.productrateplanservice.tieredpricing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/tiered")
@RequiredArgsConstructor
public class TieredPricingController {

    private final TieredPricingService tieredPricingService;

    @PostMapping
    public ResponseEntity<TieredPricingDTO> create(
            @PathVariable Long ratePlanId,
            @Valid @RequestBody TieredPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(tieredPricingService.create(ratePlanId, dto));
    }

    @PutMapping("/{tieredPricingId}")
    public ResponseEntity<TieredPricingDTO> update(
            @PathVariable Long tieredPricingId,
            @Valid @RequestBody TieredPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(tieredPricingService.update(tieredPricingId, dto));
    }

    @GetMapping
    public ResponseEntity<List<TieredPricingDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(tieredPricingService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{tieredPricingId}")
    public ResponseEntity<TieredPricingDTO> getById(@PathVariable Long tieredPricingId) {
        return ResponseEntity.ok(tieredPricingService.getById(tieredPricingId));
    }

    @DeleteMapping("/{tieredPricingId}")
    public ResponseEntity<Void> delete(@PathVariable Long tieredPricingId) {
        tieredPricingService.deleteById(tieredPricingId);
        return ResponseEntity.noContent().build();
    }
}
