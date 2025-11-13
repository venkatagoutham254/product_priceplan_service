package aforo.productrateplanservice.validation;

import aforo.productrateplanservice.stairsteppricing.StairStepTierCreateUpdateDTO;
import aforo.productrateplanservice.tieredpricing.TieredTierCreateUpdateDTO;
import aforo.productrateplanservice.volumepricing.VolumeTierCreateUpdateDTO;

import java.math.BigDecimal;

/**
 * Adapter classes to make DTOs compatible with TierValidatable interface
 */
public class TierValidationAdapters {

    public static class TieredTierAdapter implements PricingTierValidator.TierValidatable {
        private final TieredTierCreateUpdateDTO dto;

        public TieredTierAdapter(TieredTierCreateUpdateDTO dto) {
            this.dto = dto;
        }

        @Override
        public Integer getStartRange() {
            return dto.getStartRange();
        }

        @Override
        public Integer getEndRange() {
            return dto.getEndRange();
        }

        @Override
        public BigDecimal getPrice() {
            return dto.getUnitPrice();
        }
    }

    public static class VolumeTierAdapter implements PricingTierValidator.TierValidatable {
        private final VolumeTierCreateUpdateDTO dto;

        public VolumeTierAdapter(VolumeTierCreateUpdateDTO dto) {
            this.dto = dto;
        }

        @Override
        public Integer getStartRange() {
            return dto.getUsageStart();
        }

        @Override
        public Integer getEndRange() {
            return dto.getUsageEnd();
        }

        @Override
        public BigDecimal getPrice() {
            return dto.getUnitPrice();
        }
    }

    public static class StairStepTierAdapter implements PricingTierValidator.TierValidatable {
        private final StairStepTierCreateUpdateDTO dto;

        public StairStepTierAdapter(StairStepTierCreateUpdateDTO dto) {
            this.dto = dto;
        }

        @Override
        public Integer getStartRange() {
            return dto.getUsageStart();
        }

        @Override
        public Integer getEndRange() {
            return dto.getUsageEnd();
        }

        @Override
        public BigDecimal getPrice() {
            return dto.getFlatCost();
        }
    }
}
