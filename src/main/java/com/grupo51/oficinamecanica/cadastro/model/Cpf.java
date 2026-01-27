package com.grupo51.oficinamecanica.cadastro.model;

public record Cpf(String numero) {
    public Cpf {
        if (numero == null || !isValid(numero)) {
            throw new IllegalArgumentException("CPF inválido: " + numero);
        }
    }

    private static boolean isValid(String cpf) {
        // 1. Limpeza e verificações básicas
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // 2. Cálculo do 1º Dígito Verificador
            int peso = 10;
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (Character.getNumericValue(cpf.charAt(i)) * peso--);
            }
            int resto = soma % 11;
            int digito1 = (resto < 2) ? 0 : 11 - resto;

            // 3. Cálculo do 2º Dígito Verificador
            peso = 11;
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (Character.getNumericValue(cpf.charAt(i)) * peso--);
            }
            resto = soma % 11;
            int digito2 = (resto < 2) ? 0 : 11 - resto;

            // 4. Verificação final
            return Character.getNumericValue(cpf.charAt(9)) == digito1 &&
                    Character.getNumericValue(cpf.charAt(10)) == digito2;

        } catch (Exception e) {
            return false;
        }
    }
}