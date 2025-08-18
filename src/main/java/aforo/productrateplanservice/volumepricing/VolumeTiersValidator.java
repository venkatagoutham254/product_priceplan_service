package aforo.productrateplanservice.volumepricing;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VolumeTiersValidator implements ConstraintValidator<ValidVolumeTiers, VolumePricingCreateUpdateDTO> {
    @Override
    public boolean isValid(VolumePricingCreateUpdateDTO dto, ConstraintValidatorContext context) {
        List<VolumeTierCreateUpdateDTO> tiers = dto.getTiers();
        if (tiers == null || tiers.isEmpty()) return true; // @NotNull/@NotEmpty handled elsewhere

        // Must be ordered by usageStart
        int prevEnd = -1;
        Set<Integer> seenStarts = new HashSet<>();
        for (VolumeTierCreateUpdateDTO tier : tiers) {
            Integer start = tier.getUsageStart();
            Integer end = tier.getUsageEnd();

            if (start == null) return false;
            if (seenStarts.contains(start)) return false; // duplicate start
            seenStarts.add(start);

            // usageStart < usageEnd if end present
            if (end != null && start >= end) return false;

            // No overlaps: current start must be > prevEnd (if prevEnd exists)
            if (prevEnd != -1 && start < prevEnd) return false;
            if (end != null) prevEnd = end;
            else prevEnd = Integer.MAX_VALUE; // open-ended
        }
        return true;
    }
}
