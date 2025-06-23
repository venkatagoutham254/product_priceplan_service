package aforo.productrateplanservice.overagecharges;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/overagecharges")
@RequiredArgsConstructor
public class OverageChargeController {

    private final OverageChargeService service;

    @PostMapping
    public ResponseEntity<OverageChargeDTO> create(
            @PathVariable Long ratePlanId,
            @RequestBody OverageChargeCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.create(ratePlanId, dto));
    }

    @GetMapping
    public ResponseEntity<List<OverageChargeDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(service.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OverageChargeDTO> getById(@PathVariable Long ratePlanId, @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(ratePlanId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OverageChargeDTO> update(
            @PathVariable Long ratePlanId, @PathVariable Long id,
            @RequestBody OverageChargeCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(ratePlanId, id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OverageChargeDTO> partialUpdate(
            @PathVariable Long ratePlanId, @PathVariable Long id,
            @RequestBody OverageChargeCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.partialUpdate(ratePlanId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long ratePlanId, @PathVariable Long id) {
        service.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
}
