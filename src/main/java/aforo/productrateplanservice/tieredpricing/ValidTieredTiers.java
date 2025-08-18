package aforo.productrateplanservice.tieredpricing;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = TieredTiersValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidTieredTiers {
    String message() default "Invalid tiered tiers: must be ordered, non-overlapping, and startRange < endRange for each tier.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
