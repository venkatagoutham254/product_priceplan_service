package aforo.productrateplanservice.product.request;

import lombok.*;

import aforo.productrateplanservice.product.enums.FileFormat;
import aforo.productrateplanservice.product.enums.DeliveryFrequency;
import aforo.productrateplanservice.product.enums.AccessMethod;
import aforo.productrateplanservice.product.enums.CompressionFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductFlatFileRequest {
    private FileFormat format;
    private String size;
    private DeliveryFrequency deliveryFrequency;
    private AccessMethod accessMethod;
    private String retentionPolicy;
    private String fileNamingConvention;
    private CompressionFormat compressionFormat;
}
