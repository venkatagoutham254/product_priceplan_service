package aforo.productrateplanservice.freemium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rateplans/{ratePlanId}/freemiums")
@RequiredArgsConstructor
public class FreemiumController {

    private final FreemiumService freemiumService;

    @PostMapping
    public ResponseEntity<FreemiumDTO> create(
            @PathVariable Long ratePlanId,
            @RequestBody FreemiumCreateUpdateDTO request) {
        return ResponseEntity.ok(freemiumService.create(ratePlanId, request));
    }

    @GetMapping
    public ResponseEntity<List<FreemiumDTO>> getAll(@PathVariable Long ratePlanId) {
        return ResponseEntity.ok(freemiumService.getAllByRatePlanId(ratePlanId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FreemiumDTO> getById(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        return ResponseEntity.ok(freemiumService.getById(ratePlanId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FreemiumDTO> update(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody FreemiumCreateUpdateDTO request) {
        return ResponseEntity.ok(freemiumService.update(ratePlanId, id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FreemiumDTO> partialUpdate(
            @PathVariable Long ratePlanId,
            @PathVariable Long id,
            @RequestBody FreemiumCreateUpdateDTO request) {
        return ResponseEntity.ok(freemiumService.partialUpdate(ratePlanId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ratePlanId,
            @PathVariable Long id) {
        freemiumService.delete(ratePlanId, id);
        return ResponseEntity.noContent().build();
    }
}
