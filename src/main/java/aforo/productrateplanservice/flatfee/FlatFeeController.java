package aforo.productrateplanservice.flatfee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/flatfee")
@RequiredArgsConstructor
public class FlatFeeController {

    private final FlatFeeService flatFeeService;

    @PostMapping
    public ResponseEntity<FlatFeeDTO> create(
            @PathVariable Long ratePlanId,
            @RequestBody @Valid FlatFeeCreateUpdateDTO dto) {
        return ResponseEntity.ok(flatFeeService.create(ratePlanId, dto));
    }

    @PutMapping
    public ResponseEntity<FlatFeeDTO> updateFully(
            @PathVariable Long ratePlanId,
            @RequestBody @Valid FlatFeeCreateUpdateDTO dto
    ) {
        return ResponseEntity.ok(flatFeeService.updateFully(ratePlanId, dto));
    }
    
    @PatchMapping
    public ResponseEntity<FlatFeeDTO> updatePartially(
            @PathVariable Long ratePlanId,
            @RequestBody FlatFeeCreateUpdateDTO dto
    ) {
        return ResponseEntity.ok(flatFeeService.updatePartially(ratePlanId, dto));
    }
    

    @GetMapping
    public ResponseEntity<FlatFeeDTO> get(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(flatFeeService.getByRatePlanId(ratePlanId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long ratePlanId) {
        flatFeeService.deleteByRatePlanId(ratePlanId);
        return ResponseEntity.noContent().build();
    }
}
