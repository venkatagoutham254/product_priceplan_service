package aforo.productrateplanservice.stairsteppricing;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StairStepTiersValidator.class)
public @interface ValidStairStepTiers {
    String message() default "Invalid stair step tier configuration: tiers must not overlap and must be properly ordered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
