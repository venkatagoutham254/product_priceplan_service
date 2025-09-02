package aforo.productrateplanservice.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public class ProductCreateMultipart {

    @Schema(name = "request", description = "Product create JSON payload")
    private CreateProductRequest request;

    @Schema(name = "icon", type = "string", format = "binary", description = "Icon file")
    private MultipartFile icon;

    public CreateProductRequest getRequest() {
        return request;
    }

    public void setRequest(CreateProductRequest request) {
        this.request = request;
    }

    public MultipartFile getIcon() {
        return icon;
    }

    public void setIcon(MultipartFile icon) {
        this.icon = icon;
    }
}
