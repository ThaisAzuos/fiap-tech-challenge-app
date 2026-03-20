package com.grupo51.oficinamecanica.comum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator para prevenir SQL Injection através de sanitização
 * Bloqueia caracteres perigosos comuns em ataques SQL
 */
public class SqlInjectionValidator implements ConstraintValidator<NoSqlInjection, String> {

    // Caracteres e padrões perigosos para SQL
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('|(\\-\\-)|(;)|(\\|\\|)|(\\*)|(/\\*)|(\\*/)|" +
        "(DROP)|(DELETE)|(INSERT)|(UPDATE)|(SELECT)|(UNION)|(EXEC)|(EXECUTE))",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public void initialize(NoSqlInjection constraintAnnotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null é validado por @NotNull se necessário
        }

        // Verifica se contém padrões SQL perigosos
        if (SQL_INJECTION_PATTERN.matcher(value).find()) {
            addConstraintViolation(context, "Campo contém caracteres ou padrões suspeitos de SQL Injection");
            return false;
        }

        // Verifica balanço de aspas
        if (!hasBalancedQuotes(value)) {
            addConstraintViolation(context, "Campo contém aspas desbalanceadas");
            return false;
        }

        return true;
    }

    private boolean hasBalancedQuotes(String value) {
        int singleQuotes = 0;
        int doubleQuotes = 0;

        for (char c : value.toCharArray()) {
            if (c == '\'') singleQuotes++;
            if (c == '"') doubleQuotes++;
        }

        return singleQuotes % 2 == 0 && doubleQuotes % 2 == 0;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}

