package pl.mlodawski.networkdiagram.diagrammodule.validator.hexvalidators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HexColorValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {
    String message() default "Invalid hex color format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
