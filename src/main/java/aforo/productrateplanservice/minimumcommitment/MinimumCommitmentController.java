package aforo.productrateplanservice.minimumcommitment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/minimumcommitments")
@RequiredArgsConstructor
public class MinimumCommitmentController {

    private final MinimumCommitmentService service;

    @PostMapping
    public ResponseEntity<MinimumCommitmentDTO> create(@PathVariable Long ratePlanId,
                                                       @RequestBody MinimumCommitmentCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.create(ratePlanId, dto));
    }

    @GetMapping
    public ResponseEntity<List<MinimumCommitmentDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(service.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MinimumCommitmentDTO> getById(@PathVariable Long ratePlanId,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(ratePlanId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MinimumCommitmentDTO> update(@PathVariable Long ratePlanId,
                                                       @PathVariable Long id,
                                                       @RequestBody MinimumCommitmentCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.update(ratePlanId, id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MinimumCommitmentDTO> partialUpdate(@PathVariable Long ratePlanId,
                                                              @PathVariable Long id,
                                                              @RequestBody MinimumCommitmentCreateUpdateDTO dto) {
        return ResponseEntity.ok(service.partialUpdate(ratePlanId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long ratePlanId,
                                       @PathVariable Long id) {
        service.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
}
