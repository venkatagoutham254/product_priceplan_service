package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.AuthType;
import aforo.productrateplanservice.product.enums.LatencyClass;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAPIDTO {
    private Long productId;
    private String endpointUrl;
    private AuthType authType;
    private String payloadSizeMetric;
    private String rateLimitPolicy;
    private String meteringGranularity;
    private String grouping;
    private boolean cachingFlag;
    private LatencyClass latencyClass;
}
