package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductLLMTokenDTO;
import aforo.productrateplanservice.product.entity.ProductLLMToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductLLMTokenMapper {
    @Mapping(source = "product.productId", target = "productId")
    ProductLLMTokenDTO toDTO(ProductLLMToken entity);

    void updateEntity(ProductLLMTokenDTO dto, @MappingTarget ProductLLMToken entity);
    
}
