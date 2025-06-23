package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductAPIDTO;
import aforo.productrateplanservice.product.entity.ProductAPI;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAPIMapper {
    ProductAPIDTO toDTO(ProductAPI entity);
}
