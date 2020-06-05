package javax.validation;

import java.lang.annotation.Annotation;

public interface ConstraintValidator<A extends Annotation, T> {
    default void initialize(A constraintAnnotation) {
    }

    boolean isValid(T var1, ConstraintValidatorContext var2);
}
