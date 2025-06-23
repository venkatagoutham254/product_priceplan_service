package aforo.productrateplanservice.product.mapper;

import org.mapstruct.Mapper;
import aforo.productrateplanservice.product.dto.ProductDTO;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.request.CreateProductRequest;
import aforo.productrateplanservice.product.request.UpdateProductRequest;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);

    ProductDTO toDTO(Product product);

    void updateProductFromRequest(UpdateProductRequest request, @MappingTarget Product product);
}