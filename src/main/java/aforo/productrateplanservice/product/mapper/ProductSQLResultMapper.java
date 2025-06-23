package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductSQLResultDTO;
import aforo.productrateplanservice.product.entity.ProductSQLResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductSQLResultMapper {
    ProductSQLResultDTO toDTO(ProductSQLResult entity);
}
