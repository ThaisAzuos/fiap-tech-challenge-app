package com.grupo51.oficinamecanica.comum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator para prevenir XSS (Cross-Site Scripting)
 * Bloqueia tags HTML, JavaScript e outros conteúdos perigosos
 */
public class XssValidator implements ConstraintValidator<NoXss, String> {

    // Padrões perigosos para XSS
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(<script[^>]*>.*?</script>)|" +                    // <script>...</script>
        "(<iframe[^>]*>.*?</iframe>)|" +                    // <iframe>...</iframe>
        "(javascript:)|" +                                   // javascript: protocol
        "(<img[^>]*onerror[^>]*>)|" +                        // img with onerror
        "(<body[^>]*onload[^>]*>)|" +                        // body with onload
        "(<input[^>]*onfocus[^>]*>)|" +                      // input with onfocus
        "(<svg[^>]*onload[^>]*>)|" +                         // svg with onload
        "(on\\w+\\s*=)|" +                                   // event handlers (onclick, etc)
        "(<[^>]*on\\w+)|" +                                  // Any tag with on* event
        "(<embed[^>]*>)|" +                                  // <embed>
        "(<object[^>]*>)",                                   // <object>
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    @Override
    public void initialize(NoXss constraintAnnotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null é validado por @NotNull se necessário
        }

        // Verifica se contém padrões XSS perigosos
        if (XSS_PATTERN.matcher(value).find()) {
            addConstraintViolation(context, "Campo contém conteúdo suspeito de XSS");
            return false;
        }

        // Verifica caracteres de controle suspeitos
        if (containsSuspiciousControlCharacters(value)) {
            addConstraintViolation(context, "Campo contém caracteres de controle suspeitos");
            return false;
        }

        return true;
    }

    private boolean containsSuspiciousControlCharacters(String value) {
        for (char c : value.toCharArray()) {
            // Unicode characters that might be used in XSS attacks
            if (c < 32 && c != '\n' && c != '\r' && c != '\t') {
                return true;
            }
            // Null bytes
            if (c == '\0') {
                return true;
            }
        }
        return false;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}

