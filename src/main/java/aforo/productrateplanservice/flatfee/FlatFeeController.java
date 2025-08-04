package aforo.productrateplanservice.flatfee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/flatfee")
@RequiredArgsConstructor
public class FlatFeeController {

    private final FlatFeeService flatFeeService;

    @PostMapping
    public ResponseEntity<FlatFeeDTO> createFlatFee(
            @PathVariable Long ratePlanId,
            @Valid @RequestBody FlatFeeCreateUpdateDTO request
    ) {
        return ResponseEntity.ok(flatFeeService.createFlatFee(ratePlanId, request));
    }

    @PutMapping
    public ResponseEntity<FlatFeeDTO> updateFlatFee(
            @PathVariable Long ratePlanId,
            @Valid @RequestBody FlatFeeCreateUpdateDTO request
    ) {
        return ResponseEntity.ok(flatFeeService.updateFlatFee(ratePlanId, request));
    }

    @GetMapping
    public ResponseEntity<FlatFeeDTO> getFlatFeeByRatePlanId(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(flatFeeService.getFlatFeeByRatePlanId(ratePlanId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FlatFeeDTO>> getAllFlatFees() {
        return ResponseEntity.ok(flatFeeService.getAllFlatFees());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFlatFee(@PathVariable Long ratePlanId) {
        flatFeeService.deleteFlatFeeByRatePlanId(ratePlanId);
        return ResponseEntity.noContent().build();
    }
}
