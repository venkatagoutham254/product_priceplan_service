package aforo.productrateplanservice.rate_plan.service;

import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for generating unique rate plan codes.
 * 
 * Format: 6-character alphanumeric code (e.g., RP3A5K, RP9B2M)
 * 
 * The code is:
 * - Immutable: Never changes even if rate plan attributes are updated
 * - Independent: Not based on billing frequency, payment type, or product
 * - Unique: Guaranteed unique per organization
 * - Read-only: Cannot be changed by users
 * 
 * Structure: RP + 4 alphanumeric characters
 * - First 2 chars: "RP" prefix for Rate Plan
 * - Next 2 chars: Sequential base-36 counter
 * - Last 2 chars: Random alphanumeric for additional uniqueness
 */
@Service
@RequiredArgsConstructor
public class RatePlanCodeGenerationService {

    private final RatePlanRepository ratePlanRepository;
    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generate a unique 6-character rate plan code
     * Format: RP + 2 sequential chars + 2 random chars
     * Example: RP3A5K, RP9B2M, RPAA7X
     */
    public String generateRatePlanCode(RatePlan ratePlan) {
        String ratePlanCode;
        
        do {
            // Generate 4-character unique suffix
            String uniqueSuffix = generateUniqueSuffix();
            ratePlanCode = "RP" + uniqueSuffix;
            
        } while (ratePlanRepository.existsByRatePlanCodeAndOrganizationId(
                ratePlanCode, ratePlan.getOrganizationId()));

        return ratePlanCode;
    }

    /**
     * Generate 4-character unique suffix
     * Format: 2 sequential base-36 chars + 2 random alphanumeric chars
     */
    private String generateUniqueSuffix() {
        // First 2 chars: sequential base-36 (00-ZZ in base-36)
        long seq = sequenceCounter.getAndIncrement() % 1296; // 36^2 = 1296
        String seqStr = Long.toString(seq, 36).toUpperCase();
        String sequential = seqStr.length() == 1 ? "0" + seqStr : seqStr;

        // Last 2 chars: random alphanumeric for additional uniqueness
        StringBuilder randomPart = new StringBuilder(2);
        for (int i = 0; i < 2; i++) {
            randomPart.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }

        return sequential + randomPart.toString();
    }
}
