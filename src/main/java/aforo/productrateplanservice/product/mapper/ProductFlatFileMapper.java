package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductFlatFileMapper {
    @Mapping(source = "product.productId", target = "productId")
    ProductFlatFileDTO toDTO(ProductFlatFile entity);

    void updateEntity(UpdateProductFlatFileRequest request, @MappingTarget ProductFlatFile entity);
}
