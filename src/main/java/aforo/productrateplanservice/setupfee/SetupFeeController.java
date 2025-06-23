package aforo.productrateplanservice.setupfee;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/setupfees")
@RequiredArgsConstructor
public class SetupFeeController {

    private final SetupFeeService setupFeeService;

    @PostMapping
    public ResponseEntity<SetupFeeDTO> create(
            @PathVariable Long ratePlanId,
            @RequestBody SetupFeeCreateUpdateDTO request) {
        return ResponseEntity.ok(setupFeeService.create(ratePlanId, request));
    }

    @GetMapping
    public ResponseEntity<List<SetupFeeDTO>> getAllByRatePlan(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(setupFeeService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetupFeeDTO> getById(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        return ResponseEntity.ok(setupFeeService.getById(ratePlanId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetupFeeDTO> update(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody SetupFeeCreateUpdateDTO request) {
        return ResponseEntity.ok(setupFeeService.update(ratePlanId, id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SetupFeeDTO> partialUpdate(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody SetupFeeCreateUpdateDTO request) {
        return ResponseEntity.ok(setupFeeService.partialUpdate(ratePlanId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        setupFeeService.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
    
}
