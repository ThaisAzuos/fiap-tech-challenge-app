package com.grupo51.oficinamecanica.comum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotação para validar SQL Injection
 * Use em String fields para proteger contra ataques SQL
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SqlInjectionValidator.class)
@Documented
public @interface NoSqlInjection {
    String message() default "Campo contém padrão suspeito de SQL Injection";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

