package aforo.productrateplanservice.stairsteppricing;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public StairStepPricingDTO update(@PathVariable Long ratePlanId,
                                      @PathVariable Long id,
                                      @RequestBody StairStepPricingCreateUpdateDTO dto) {
        return service.update(ratePlanId, id, dto);
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
