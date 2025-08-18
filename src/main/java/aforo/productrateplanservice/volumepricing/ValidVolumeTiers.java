package aforo.productrateplanservice.volumepricing;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = VolumeTiersValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidVolumeTiers {
    String message() default "Invalid volume tiers: must be ordered, non-overlapping, and usageStart < usageEnd for each tier.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
