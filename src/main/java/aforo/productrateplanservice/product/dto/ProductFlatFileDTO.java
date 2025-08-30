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

    private String fileLocation;
}
