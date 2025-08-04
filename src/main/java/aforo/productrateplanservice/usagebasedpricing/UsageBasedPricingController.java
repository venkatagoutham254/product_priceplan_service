package aforo.productrateplanservice.usagebasedpricing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/usagebased")
@RequiredArgsConstructor
public class UsageBasedPricingController {

    private final UsageBasedPricingService usageBasedPricingService;

    @PostMapping
    public ResponseEntity<UsageBasedPricingDTO> create(
            @PathVariable Long ratePlanId,
            @Valid @RequestBody UsageBasedPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(usageBasedPricingService.create(ratePlanId, dto));
    }

    @PutMapping("/{usageBasedPricingId}")
    public ResponseEntity<UsageBasedPricingDTO> update(
            @PathVariable Long usageBasedPricingId,
            @Valid @RequestBody UsageBasedPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(usageBasedPricingService.update(usageBasedPricingId, dto));
    }

    @GetMapping
    public ResponseEntity<List<UsageBasedPricingDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(usageBasedPricingService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{usageBasedPricingId}")
    public ResponseEntity<UsageBasedPricingDTO> getById(@PathVariable Long usageBasedPricingId) {
        return ResponseEntity.ok(usageBasedPricingService.getById(usageBasedPricingId));
    }

    @DeleteMapping("/{usageBasedPricingId}")
    public ResponseEntity<Void> delete(@PathVariable Long usageBasedPricingId) {
        usageBasedPricingService.deleteById(usageBasedPricingId);
        return ResponseEntity.noContent().build();
    }
}
