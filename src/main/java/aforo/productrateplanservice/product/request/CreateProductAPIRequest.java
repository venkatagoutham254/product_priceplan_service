package aforo.productrateplanservice.product.request;

import lombok.*;
import aforo.productrateplanservice.product.enums.AuthType;
import aforo.productrateplanservice.product.enums.LatencyClass;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductAPIRequest {
    
    private String endpointUrl;
    private AuthType authType;
    private String payloadSizeMetric;
    private String rateLimitPolicy;
    private String meteringGranularity;
    private String grouping;
    private boolean cachingFlag;
    private LatencyClass latencyClass;
}
