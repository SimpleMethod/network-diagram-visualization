package pl.mlodawski.networkdiagram.diagrammodule.validator.hexvalidators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class HexColorValidator implements ConstraintValidator<HexColor, String> {
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    @Override
    public void initialize(HexColor constraintAnnotation) {

    }

    @Override
    public boolean isValid(String colorField, ConstraintValidatorContext constraintValidatorContext) {
        return colorField == null || HEX_COLOR_PATTERN.matcher(colorField).matches();
    }
}
