package com.grupo51.oficinamecanica.comum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotação para validar XSS (Cross-Site Scripting)
 * Use em String fields para proteger contra ataques XSS
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XssValidator.class)
@Documented
public @interface NoXss {
    String message() default "Campo contém padrão suspeito de XSS";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

