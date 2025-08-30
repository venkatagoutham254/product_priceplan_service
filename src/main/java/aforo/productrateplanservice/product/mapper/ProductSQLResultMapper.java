package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductSQLResultMapper {
    @Mapping(source = "product.productId", target = "productId")
    ProductSQLResultDTO toDTO(ProductSQLResult entity);

    void updateEntity(UpdateProductSQLResultRequest request, @MappingTarget ProductSQLResult entity);
}
