package aforo.productrateplanservice.discount;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountDTO> create(
            @PathVariable Long ratePlanId,
            @RequestBody DiscountCreateUpdateDTO dto) {
        return ResponseEntity.ok(discountService.create(ratePlanId, dto));
    }

    @GetMapping
    public ResponseEntity<List<DiscountDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(discountService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountDTO> getById(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        return ResponseEntity.ok(discountService.getById(ratePlanId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountDTO> update(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody DiscountCreateUpdateDTO dto) {
        return ResponseEntity.ok(discountService.update(ratePlanId, id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DiscountDTO> partialUpdate(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody DiscountCreateUpdateDTO dto) {
        return ResponseEntity.ok(discountService.partialUpdate(ratePlanId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        discountService.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
}
