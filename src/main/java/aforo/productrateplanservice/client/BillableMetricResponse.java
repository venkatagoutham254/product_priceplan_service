package aforo.productrateplanservice.client;



import lombok.Data;
import java.util.List;

@Data
public class BillableMetricResponse {
    private Long metricId;
    private String metricName;
    private Long productId;
    private String version;
    private String unitOfMeasure;
    private String description;
    private String aggregationFunction;
    private String aggregationWindow;
    private List<UsageConditionDTO> usageConditions;
}

@Data
class UsageConditionDTO {
    private String dimension;
    private String operator;
    private String value;
}
