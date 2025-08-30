package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductStorageDTO;
import aforo.productrateplanservice.product.entity.ProductStorage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import aforo.productrateplanservice.product.request.UpdateProductStorageRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductStorageMapper {
    @Mapping(source = "product.productId", target = "productId")
    ProductStorageDTO toDTO(ProductStorage entity);

    void updateEntity(UpdateProductStorageRequest request, @MappingTarget ProductStorage entity);
}
