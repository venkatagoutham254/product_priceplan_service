package aforo.productrateplanservice.validation;

import aforo.productrateplanservice.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic validator for pricing tiers.
 * Provides detailed validation with specific error messages.
 */
@Component
public class PricingTierValidator {

    /**
     * Validates tiered pricing business rules
     */
    public void validateTieredPricingTiers(List<? extends TierValidatable> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new ValidationException("At least one pricing tier is required");
        }

        validateTierSequence(tiers, "Tiered pricing");
        validateNoDuplicateRanges(tiers, "Tiered pricing");
        validatePositivePrices(tiers, "Tiered pricing");
    }

    /**
     * Validates volume pricing business rules
     */
    public void validateVolumePricingTiers(List<? extends TierValidatable> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new ValidationException("At least one volume tier is required");
        }

        validateTierSequence(tiers, "Volume pricing");
        validateNoDuplicateRanges(tiers, "Volume pricing");
        validatePositivePrices(tiers, "Volume pricing");
    }

    /**
     * Validates stair step pricing business rules
     */
    public void validateStairStepPricingTiers(List<? extends TierValidatable> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new ValidationException("At least one stair step tier is required");
        }

        validateTierSequence(tiers, "Stair step pricing");
        validateNoDuplicateRanges(tiers, "Stair step pricing");
        validatePositivePrices(tiers, "Stair step pricing");
    }

    /**
     * Validates that tiers are in proper sequence without gaps or overlaps
     */
    private void validateTierSequence(List<? extends TierValidatable> tiers, String pricingType) {
        TierValidatable prevTier = null;
        
        for (int i = 0; i < tiers.size(); i++) {
            TierValidatable currentTier = tiers.get(i);
            
            // Validate start range is non-negative
            if (currentTier.getStartRange() < 0) {
                throw new ValidationException(String.format(
                    "%s tier %d: Start range cannot be negative (got: %d)", 
                    pricingType, i + 1, currentTier.getStartRange()));
            }
            
            // Validate end range if present
            if (currentTier.getEndRange() != null) {
                if (currentTier.getEndRange() <= currentTier.getStartRange()) {
                    throw new ValidationException(String.format(
                        "%s tier %d: End range (%d) must be greater than start range (%d)", 
                        pricingType, i + 1, currentTier.getEndRange(), currentTier.getStartRange()));
                }
            }
            
            // Validate sequence with previous tier
            if (prevTier != null) {
                if (currentTier.getStartRange() <= prevTier.getStartRange()) {
                    throw new ValidationException(String.format(
                        "%s tier %d: Start range (%d) must be greater than previous tier start range (%d)", 
                        pricingType, i + 1, currentTier.getStartRange(), prevTier.getStartRange()));
                }
                
                // Check for gaps or overlaps
                Integer prevEnd = prevTier.getEndRange();
                if (prevEnd != null && currentTier.getStartRange() < prevEnd) {
                    throw new ValidationException(String.format(
                        "%s tier %d: Start range (%d) overlaps with previous tier end range (%d)", 
                        pricingType, i + 1, currentTier.getStartRange(), prevEnd));
                }
            }
            
            prevTier = currentTier;
        }
    }

    /**
     * Validates no duplicate start ranges
     */
    private void validateNoDuplicateRanges(List<? extends TierValidatable> tiers, String pricingType) {
        for (int i = 0; i < tiers.size(); i++) {
            for (int j = i + 1; j < tiers.size(); j++) {
                if (tiers.get(i).getStartRange().equals(tiers.get(j).getStartRange())) {
                    throw new ValidationException(String.format(
                        "%s: Duplicate start range found: %d (tiers %d and %d)", 
                        pricingType, tiers.get(i).getStartRange(), i + 1, j + 1));
                }
            }
        }
    }

    /**
     * Validates all prices are positive
     */
    private void validatePositivePrices(List<? extends TierValidatable> tiers, String pricingType) {
        for (int i = 0; i < tiers.size(); i++) {
            TierValidatable tier = tiers.get(i);
            if (tier.getPrice() == null || tier.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(String.format(
                    "%s tier %d: Price must be greater than 0 (got: %s)", 
                    pricingType, i + 1, tier.getPrice()));
            }
        }
    }

    /**
     * Interface for tier validation
     */
    public interface TierValidatable {
        Integer getStartRange();
        Integer getEndRange();
        BigDecimal getPrice();
    }
}
