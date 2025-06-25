package aforo.productrateplanservice.stairsteppricing;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/stair-step-pricing")
@RequiredArgsConstructor
public class StairStepPricingController {

    private final StairStepPricingService service;

    @PostMapping
    public StairStepPricingDTO create(@PathVariable Long ratePlanId,
                                      @RequestBody StairStepPricingCreateUpdateDTO dto) {
        return service.create(ratePlanId, dto);
    }

@PutMapping("/{id}")
public ResponseEntity<StairStepPricingDTO> updateFully(
        @PathVariable Long ratePlanId,
        @PathVariable Long id,
        @RequestBody @Valid StairStepPricingCreateUpdateDTO dto
) {
    return ResponseEntity.ok(service.updateFully(ratePlanId, id, dto));
}

@PatchMapping("/{id}")
public ResponseEntity<StairStepPricingDTO> updatePartially(
        @PathVariable Long ratePlanId,
        @PathVariable Long id,
        @RequestBody StairStepPricingCreateUpdateDTO dto
) {
    return ResponseEntity.ok(service.updatePartially(ratePlanId, id, dto));
}

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long ratePlanId,
                       @PathVariable Long id) {
        service.delete(ratePlanId, id);
    }

    @GetMapping
    public List<StairStepPricingDTO> getByRatePlan(@PathVariable Long ratePlanId) {
        return service.getByRatePlanId(ratePlanId);
    }

    @GetMapping("/{id}")
    public StairStepPricingDTO getById(@PathVariable Long ratePlanId,
                                       @PathVariable Long id) {
        return service.getById(ratePlanId, id);
    }
}
