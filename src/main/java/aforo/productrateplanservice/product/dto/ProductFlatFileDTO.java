package aforo.productrateplanservice.product.dto;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFlatFileDTO {
    private Long productId;
    private FileFormat format;
    private String size;
    private DeliveryFrequency deliveryFrequency;
    private AccessMethod accessMethod;
    private String retentionPolicy;
    private String fileNamingConvention;
    private CompressionFormat compressionFormat;
}
