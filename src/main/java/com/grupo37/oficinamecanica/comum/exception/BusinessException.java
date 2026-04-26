package com.grupo37.oficinamecanica.comum.exception;

// Exceção customizada para regras de negócio
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
