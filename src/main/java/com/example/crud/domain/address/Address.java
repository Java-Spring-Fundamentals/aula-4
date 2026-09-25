package com.example.crud.domain.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Representa o JSON devolvido pela ViaCEP.
// - @NoArgsConstructor: o Jackson precisa de um construtor vazio para montar o objeto.
// - @JsonIgnoreProperties: se a ViaCEP passar a mandar algum campo novo, ele é ignorado
//   em vez de quebrar a conversão.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String cep;
    private String logradouro;
    private String complemento;
    private String unidade;
    private String bairro;
    private String localidade;
    private String uf;
    private String estado;
    private String regiao;
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;

    // Quando o CEP tem formato válido mas não existe, a ViaCEP responde
    // status 200 com o corpo {"erro": "true"}.
    private Boolean erro;
}
