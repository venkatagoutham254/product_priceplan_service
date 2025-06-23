package aforo.productrateplanservice.freemium;

import aforo.productrateplanservice.freemium.FreemiumCreateUpdateDTO;
import aforo.productrateplanservice.freemium.FreemiumDTO;
import org.springframework.stereotype.Component;

@Component
public class FreemiumMapper {

    public FreemiumDTO toDTO(Freemium entity) {
        FreemiumDTO dto = new FreemiumDTO();
        dto.setId(entity.getId());
        dto.setFreemiumType(entity.getFreemiumType());
        dto.setFreeUnits(entity.getFreeUnits());
        dto.setFreeTrialDuration(entity.getFreeTrialDuration());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    public void update(Freemium entity, FreemiumCreateUpdateDTO dto) {
        entity.setFreemiumType(dto.getFreemiumType());
        entity.setFreeUnits(dto.getFreeUnits());
        entity.setFreeTrialDuration(dto.getFreeTrialDuration());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }
}
