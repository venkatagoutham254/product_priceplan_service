package aforo.productrateplanservice.client;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class BillableMetricResponse {
    @JsonProperty("billableMetricId")
    private Long metricId; // mapped from billableMetricId
    private String metricName;
    private Long productId;
    private String version;
    private String unitOfMeasure;
    private String description;
    private String aggregationFunction;
    private String aggregationWindow;
    private String status; // DRAFT/ACTIVE, used for filtering
    private List<UsageConditionDTO> usageConditions;
}

@Data
class UsageConditionDTO {
    private String dimension;
    private String operator;
    private String value;
}
