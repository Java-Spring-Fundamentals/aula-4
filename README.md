# Produto API

API REST em Spring Boot para cadastro de produtos, com uma integração extra: dado um CEP e o id de um produto, ela consulta o ViaCEP e diz se aquele produto está disponível na cidade correspondente ao CEP (comparando com o centro de distribuição do produto).

## Pré-requisitos

- Java 17
- Docker (para subir o PostgreSQL)
- Maven (opcional, o projeto já vem com o `mvnw`)

## Como rodar

1. Suba o banco de dados:

```bash
docker compose up -d
```

2. Rode a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`.

O Flyway cria a tabela `product` automaticamente e já popula com alguns produtos de exemplo, distribuídos entre os três centros de distribuição (Mogi das Cruzes, Recife e Porto Alegre).

A aplicação sobe em `http://localhost:8080`.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/produtos` | Cria um produto |
| GET | `/produtos` | Lista todos os produtos (aceita `?categoria=` como filtro) |
| GET | `/produtos/top5` | Retorna os 5 produtos ativos mais caros |
| GET | `/produtos/{id}` | Busca um produto pelo id |
| PUT | `/produtos/{id}` | Atualiza um produto |
| PATCH | `/produtos/{id}/inativar` | Inativa um produto (soft delete) |
| GET | `/produtos/{id}/disponibilidade?cep=00000000` | Consulta o ViaCEP e compara a cidade do CEP com o centro de distribuição do produto |

### Exemplo de corpo para criar/atualizar produto

```json
{
  "name": "Mouse sem fio",
  "category": "Eletronicos",
  "price": 129.90,
  "distributionCenter": "Mogi das Cruzes"
}
```

`distributionCenter` só aceita: `Mogi das Cruzes`, `Recife` ou `Porto Alegre`.

### Tratamento de erros

- CEP mal formatado → `400 Bad Request`
- CEP que não existe → `400 Bad Request`
- ViaCEP fora do ar / timeout → `503 Service Unavailable`
- Produto não encontrado → `404 Not Found`
- Dados inválidos no corpo da requisição → `400 Bad Request` com o detalhe de cada campo

## Postman

A collection usada nos testes está em `postman/produto-api.postman_collection.json`. Ela já cobre o fluxo feliz e os cenários de erro (CEP inválido, CEP inexistente e produto não encontrado). Basta importar no Postman; a variável `baseUrl` já vem configurada para `http://localhost:8080`.
