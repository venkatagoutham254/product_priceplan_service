package aforo.productrateplanservice.volumepricing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/volume-pricing")
@RequiredArgsConstructor
public class VolumePricingController {

    private final VolumePricingService volumePricingService;

    @PostMapping
    public ResponseEntity<VolumePricingDTO> createVolumePricing(
            @PathVariable Long ratePlanId,
            @RequestBody @Valid VolumePricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(volumePricingService.create(ratePlanId, dto));
    }

    @PutMapping("/{volumePricingId}")
    public ResponseEntity<VolumePricingDTO> updateVolumePricing(
            @PathVariable Long ratePlanId,
            @PathVariable Long volumePricingId,
            @RequestBody @Valid VolumePricingCreateUpdateDTO dto) {
        return ResponseEntity.ok(volumePricingService.update(ratePlanId, volumePricingId, dto));
    }

    @GetMapping("/{volumePricingId}")
    public ResponseEntity<VolumePricingDTO> getById(
            @PathVariable Long volumePricingId) {
        return ResponseEntity.ok(volumePricingService.getById(volumePricingId));
    }

    @GetMapping
    public ResponseEntity<List<VolumePricingDTO>> getAllByRatePlanId(
            @PathVariable Long ratePlanId) {
        return ResponseEntity.ok(volumePricingService.getAllByRatePlanId(ratePlanId));
    }

    @DeleteMapping("/{volumePricingId}")
    public ResponseEntity<Void> delete(@PathVariable Long volumePricingId) {
        volumePricingService.deleteById(volumePricingId);
        return ResponseEntity.noContent().build();
    }
}
