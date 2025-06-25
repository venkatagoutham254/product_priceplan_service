package aforo.productrateplanservice.product.mapper;

import aforo.productrateplanservice.product.dto.ProductFlatFileDTO;
import aforo.productrateplanservice.product.entity.ProductFlatFile;
import aforo.productrateplanservice.product.request.UpdateProductFlatFileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductFlatFileMapper {
    ProductFlatFileDTO toDTO(ProductFlatFile entity);
    
    void updateEntity(@MappingTarget ProductFlatFile existing, UpdateProductFlatFileRequest request);
    void partialUpdate(@MappingTarget ProductFlatFile existing, UpdateProductFlatFileRequest request);
}
