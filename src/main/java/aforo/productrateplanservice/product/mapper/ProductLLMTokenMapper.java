package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductLLMTokenMapper {
    ProductLLMTokenDTO toDTO(ProductLLMToken entity);
}
