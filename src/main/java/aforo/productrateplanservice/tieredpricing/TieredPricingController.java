package aforo.productrateplanservice.tieredpricing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/tiered-pricing")
@RequiredArgsConstructor
public class TieredPricingController {

    private final TieredPricingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TieredPricingDTO create(@PathVariable Long ratePlanId, @RequestBody TieredPricingCreateUpdateDTO dto) {
        return service.create(ratePlanId, dto);
    }

    @GetMapping
    public List<TieredPricingDTO> getAll(@PathVariable Long ratePlanId) {
        return service.getByRatePlanId(ratePlanId);
    }

    @PutMapping("/{id}")
    public TieredPricingDTO update(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody TieredPricingCreateUpdateDTO dto
    ) {
        return service.update(ratePlanId, id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
