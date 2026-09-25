package com.example.crud.service;


import com.example.crud.domain.address.Address;
import com.example.crud.domain.product.Product;
import com.example.crud.infra.CepInvalidoException;
import com.example.crud.infra.CepNaoEncontradoException;
import com.example.crud.infra.ViaCepIndisponivelException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service // Para avisa o spring que essa é uma classe de serviço
public class ViaCepService {

    private final RestTemplate restTemplate; // Ferramenta Spring para fazer requisições HTTP, é a parte que integra nossa API com a API externa

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate; //
    }

    public Address buscarEndereco(String cep) {
        //Validar o formato antes de chamar a API do viaCEP
        String cepLimpo = cep.replace("-", ""); // tirar traço do cep
        if (!cepLimpo.matches("\\d{8}")){ // evitar expressões do tipo 123456
            throw new CepInvalidoException("CEP inválido: Ele deve conter 8 numeroooos");
        }

        //Chamar a API do via CEP e tratar falhas se o serviço estiver indisponivel
        String url = "https://viacep.com.br/ws/{cep}/json/";
        Address endereco;
        try {
            endereco = restTemplate.getForObject(url, Address.class, cepLimpo);
        } catch (RestClientException e) {
            throw new ViaCepIndisponivelException("Serviço VIACEP neste momento está indisponível, tente novamente mais tarde");
        }

        //Caso o CEP exista mas está com formato errado
        if (endereco == null || endereco.getLocalidade() == null){
            throw new CepNaoEncontradoException("Este CEP não foi encontrado: " +cep);
        }
        return  endereco;
        }


    //Adicionando o serviço de verificardisponibilidade
    public boolean verificarDisponibilidade(String cep, Product produto){ // Recebendo o CEP e o produto inteiro
        Address endereco = buscarEndereco(cep);

        return endereco.getLocalidade().equalsIgnoreCase(produto.getDistribution_center()); //compara dois textos ignorando letras maiusculas e minusculas, por exemplo: mogi das cruzes é a mesma coisa de Mogi Das Cruzes
    }   //Lembrando que os nomes gravados no banco (cidades) n tem acentos então funciona perfeitamente o IgnoreCase
}

// Por curiosidade existem novos metodos, como RestClient e WebClient, mas como estamos usando java 21 escolhi usar o
// metodo RestTemplate que era oq existia na época