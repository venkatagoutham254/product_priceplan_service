package aforo.productrateplanservice.usagebasedpricing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/usage-based")
@RequiredArgsConstructor
public class UsageBasedPricingController {

    private final UsageBasedPricingService service;

    @PostMapping
    public ResponseEntity<UsageBasedPricingDTO> create(@PathVariable Long ratePlanId,
                                                       @RequestBody UsageBasedPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.create(ratePlanId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsageBasedPricingDTO> update(@PathVariable Long ratePlanId,
                                                       @PathVariable Long id,
                                                       @RequestBody UsageBasedPricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(ratePlanId, id, dto));
    }

    @GetMapping
    public ResponseEntity<UsageBasedPricingDTO> getByRatePlanId(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(service.getByRatePlanId(ratePlanId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long ratePlanId,
                                       @PathVariable Long id) {
        service.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
}
