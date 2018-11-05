package io.github.frapples.javaf4dpocket.comm.utils;

import java.util.Set;
import java.util.function.Function;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 18-11-4
 */
public class ValidationUtils {

    private static Validator validator;
    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ValidationUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> Set<ConstraintViolation<T>> valide(T o) {
        return validator.validate(o);
    }

    public static <T> boolean isValid(T o) {
        return validator.validate(o).isEmpty();
    }

    public static <T, E extends RuntimeException> void validOrThrow(T o, Function<String, E> exceptionNew) {
        if (!validator.validate(o).isEmpty()) {
            throw exceptionNew.apply("参数错误");
        }
    }
}
