package com.grupo37.oficinamecanica.cadastro.domain.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor // <-- Isso gera o construtor com todos os campos
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endereco {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
}