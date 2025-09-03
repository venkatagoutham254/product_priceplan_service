package aforo.productrateplanservice.client;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@Data
@JsonPropertyOrder({
        "metricId",
        "metricName",
        "productId",
        "version",
        "unitOfMeasure",
        "description",
        "aggregationFunction",
        "aggregationWindow",
        "status",
        "usageConditions"
})
public class BillableMetricResponse {
    @JsonAlias("billableMetricId")
    private Long metricId; // accept billableMetricId on input; serialize as metricId
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
