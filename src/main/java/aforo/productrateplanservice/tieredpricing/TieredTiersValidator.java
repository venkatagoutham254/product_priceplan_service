package aforo.productrateplanservice.tieredpricing;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TieredTiersValidator implements ConstraintValidator<ValidTieredTiers, TieredPricingCreateUpdateDTO> {
    @Override
    public boolean isValid(TieredPricingCreateUpdateDTO dto, ConstraintValidatorContext context) {
        List<TieredTierCreateUpdateDTO> tiers = dto.getTiers();
        if (tiers == null || tiers.isEmpty()) return true; // @NotNull/@NotEmpty handled elsewhere

        int prevEnd = -1;
        Set<Integer> seenStarts = new HashSet<>();
        for (TieredTierCreateUpdateDTO tier : tiers) {
            Integer start = tier.getStartRange();
            Integer end = tier.getEndRange();

            if (start == null) return false;
            if (seenStarts.contains(start)) return false; // duplicate start
            seenStarts.add(start);

            // startRange < endRange if end present
            if (end != null && start >= end) return false;

            // No overlaps: current start must be > prevEnd (if prevEnd exists)
            if (prevEnd != -1 && start < prevEnd) return false;
            if (end != null) prevEnd = end;
            else prevEnd = Integer.MAX_VALUE; // open-ended
        }
        return true;
    }
}
