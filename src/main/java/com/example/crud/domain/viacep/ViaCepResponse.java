package com.example.crud.domain.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ViaCepResponse {
    private String cep;
    private String logradouro;
    private String localidade;
    private String uf;
    private Boolean erro;

    // getters e setters
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public Boolean getErro() { return erro; }
    public void setErro(Boolean erro) { this.erro = erro; }

    public boolean isErro() {
        return Boolean.TRUE.equals(erro);
    }
}