package aforo.productrateplanservice.volumepricing;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class VolumePricingMapper {

    public VolumePricingDTO toDTO(VolumePricing entity) {
        VolumePricingDTO dto = new VolumePricingDTO();
        dto.setId(entity.getId());
        dto.setStartRange(entity.getStartRange());
        dto.setEndRange(entity.getEndRange());
        dto.setUnitPrice(entity.getUnitPrice());
        return dto;
    }

    public void updateEntity(VolumePricing entity, VolumePricingCreateUpdateDTO dto) {
        entity.setStartRange(dto.getStartRange());
        entity.setEndRange(dto.getEndRange());
        entity.setUnitPrice(dto.getUnitPrice());
    }
}
