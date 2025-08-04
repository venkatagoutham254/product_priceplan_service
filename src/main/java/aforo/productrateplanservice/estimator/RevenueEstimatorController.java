package aforo.productrateplanservice.estimator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/estimator", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Revenue Estimator", description = "Estimate revenue for a given rate plan and usage")
public class RevenueEstimatorController {

    private final RevenueEstimatorService estimatorService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Estimate revenue",
            description = "Returns a detailed line-item cost breakdown for the supplied usage and toggles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful estimate", 
                            content = @Content(schema = @Schema(implementation = EstimateResponse.class)))
            })
    public EstimateResponse estimate(@Valid @RequestBody EstimateRequest request) {
        return estimatorService.estimate(request);
    }
}
