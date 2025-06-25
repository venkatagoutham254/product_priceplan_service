package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import aforo.productrateplanservice.product.request.UpdateProductSQLResultRequest;
import aforo.productrateplanservice.product.request.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductSQLResultMapper {
    ProductSQLResultDTO toDTO(ProductSQLResult entity);
    ProductSQLResultDTO toEntity(ProductSQLResultDTO DTO);
    
    void partialUpdate(@MappingTarget ProductSQLResult existing, UpdateProductSQLResultRequest request);
    void updateEntity(@MappingTarget ProductSQLResult existing, UpdateProductSQLResultRequest request);
}
