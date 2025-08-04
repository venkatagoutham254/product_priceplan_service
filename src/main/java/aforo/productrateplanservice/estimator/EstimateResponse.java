package aforo.productrateplanservice.estimator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateResponse {

    private String modelType;         // e.g. "FLATFEE", "USAGE_BASED", etc.
    private List<LineItem> breakdown; // All line items
    private BigDecimal total;         // Final sum after all additions/deductions

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private String label;
        private String calculation;
        private BigDecimal amount;    // positive for charges, negative for credits/discounts
    }
}
