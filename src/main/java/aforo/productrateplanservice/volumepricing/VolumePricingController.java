package aforo.productrateplanservice.volumepricing;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/volume-pricing")
@RequiredArgsConstructor
public class VolumePricingController {

    private final VolumePricingService service;

    @PostMapping
    public VolumePricingDTO create(@PathVariable Long ratePlanId,
                                   @RequestBody VolumePricingCreateUpdateDTO dto) {
        return service.create(ratePlanId, dto);
    }

    @GetMapping
    public List<VolumePricingDTO> getByRatePlanId(@PathVariable Long ratePlanId) {
        return service.getByRatePlanId(ratePlanId);
    }

@PutMapping("/{id}")
public ResponseEntity<VolumePricingDTO> updateFully(
        @PathVariable Long ratePlanId,
        @PathVariable Long id,
        @RequestBody @Valid VolumePricingCreateUpdateDTO dto
) {
    return ResponseEntity.ok(service.updateFully(ratePlanId, id, dto));
}

@PatchMapping("/{id}")
public ResponseEntity<VolumePricingDTO> updatePartially(
        @PathVariable Long ratePlanId,
        @PathVariable Long id,
        @RequestBody VolumePricingCreateUpdateDTO dto
) {
    return ResponseEntity.ok(service.updatePartially(ratePlanId, id, dto));
}


@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(
    @PathVariable Long ratePlanId,
    @PathVariable Long id) {
    service.delete(ratePlanId, id);
    return ResponseEntity.noContent().build();
}


    @DeleteMapping
    public void deleteAllByRatePlan(@PathVariable Long ratePlanId) {
        service.deleteByRatePlanId(ratePlanId);
    }
}
