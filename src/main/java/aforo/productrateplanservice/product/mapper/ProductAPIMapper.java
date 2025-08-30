package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.entity.ProductAPI;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import aforo.productrateplanservice.product.request.UpdateProductAPIRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductAPIMapper {
    @Mapping(source = "product.productId", target = "productId")
    ProductAPIDTO toDTO(ProductAPI entity);

    void updateEntity(UpdateProductAPIRequest request, @MappingTarget ProductAPI entity);
}
