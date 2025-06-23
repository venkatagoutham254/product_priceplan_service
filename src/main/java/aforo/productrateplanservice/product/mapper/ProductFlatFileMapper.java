package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductFlatFileMapper {
    ProductFlatFileDTO toDTO(ProductFlatFile entity);
}
