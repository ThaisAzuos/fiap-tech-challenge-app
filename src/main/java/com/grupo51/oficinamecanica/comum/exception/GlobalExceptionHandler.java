package com.grupo51.oficinamecanica.comum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException ex) {
        // Transforma o erro interno em um 400 Bad Request com a mensagem do CPF
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        // Se for erro de horário, devolve Conflict (409), senão devolve Unprocessable (422)
        HttpStatus status = ex.getMessage().contains("Recurso ocupado")
                ? HttpStatus.CONFLICT
                : HttpStatus.UNPROCESSABLE_ENTITY;

        return ResponseEntity.status(status).body(ex.getMessage());
    }
}
