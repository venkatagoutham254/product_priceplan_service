package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mappings({
        @Mapping(target = "productId", ignore = true),
        @Mapping(target = "status", constant = "DRAFT"),
        @Mapping(target = "createdOn", ignore = true),
        @Mapping(target = "lastUpdated", ignore = true)
    })
    Product toEntity(CreateProductRequest request);

    ProductDTO toDTO(Product product);

    void updateProductFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}
