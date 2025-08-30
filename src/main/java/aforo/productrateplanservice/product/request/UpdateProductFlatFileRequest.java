package aforo.productrateplanservice.product.request;

import aforo.productrateplanservice.product.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductFlatFileRequest {
    private FileFormat format;
    private String fileLocation;
}
