package aforo.productrateplanservice.product.request;

import lombok.*;

import aforo.productrateplanservice.product.enums.FileFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductFlatFileRequest {
    private FileFormat format;
    private String fileLocation;
}
