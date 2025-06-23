package aforo.productrateplanservice.rate_plan;

import org.springframework.stereotype.Component;

@Component
public class RatePlanAssembler {

    public CreateRatePlanRequest toCreateRequest(RatePlanDTO dto) {
        return CreateRatePlanRequest.builder()
                .ratePlanName(dto.getRatePlanName())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .ratePlanType(dto.getRatePlanType())
                .billingFrequency(dto.getBillingFrequency())
                .build();
    }
}
