# integration-viacep

API REST em Java + Spring Boot para cadastro de produtos, com um endpoint de
integração com o serviço [ViaCEP](https://viacep.com.br/) que verifica se um
produto está disponível na cidade de um CEP.

## Stack

- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- PostgreSQL
- Flyway (migrations)
- Maven

## Como rodar

1. Suba o banco com Docker:

   ```
   docker-compose up -d
   ```

   Isso cria um Postgres em `localhost:5432` (banco `product`, usuário
   `postgres`, senha `password`) e um pgAdmin em `localhost:15432`.

2. Confira se `src/main/resources/application.properties` aponta para o
   mesmo banco/usuário/senha do `docker-compose.yml`.

3. Rode a aplicação:

   ```
   ./mvnw spring-boot:run
   ```

   O Flyway aplica as migrations automaticamente na subida. A API fica
   disponível em `http://localhost:8080`.

## Endpoints

| Método | Rota                                   | Descrição                                                   |
|--------|----------------------------------------|-------------------------------------------------------------|
| GET    | `/product`                             | Lista todos os produtos ativos                              |
| POST   | `/product`                             | Cadastra um novo produto                                    |
| PUT    | `/product`                             | Atualiza um produto                                         |
| DELETE | `/product/{id}`                        | Inativa um produto (soft delete)                            |
| GET    | `/product/availability/{id}?cep={cep}` | **Verifica se o produto está disponível na cidade do CEP**  |
| GET    | `/product/endpoint1?categoryAsParam=`  | Lista produtos ativos de uma categoria                      |
| GET    | `/product/endpoint2/{id}`              | Busca um produto por id                                     |
| GET    | `/product/endpoint3/top5byprice`       | Top 5 produtos por preço                                    |
| GET    | `/product/category/{categoryAsPath}`   | Exemplo combinando Path Variable, Request Param, Header e Body |
| GET    | `/product/cep`                         | Busca CEP a partir de estado/cidade/rua (uso didático)      |

## Disponibilidade por CEP (Aula 4)

```
GET /product/availability/p1?cep=08773380
```

```json
{
  "productId": "p1",
  "cep": "08773380",
  "cepCity": "Mogi das Cruzes",
  "distributionCenter": "Mogi das Cruzes",
  "available": true
}
```

Como funciona:

1. O produto é buscado no banco (inexistente ou inativado → `404`).
2. O `ViaCepService` valida o formato do CEP e consulta
   `https://viacep.com.br/ws/{cep}/json/` para descobrir a cidade.
3. O `AvailabilityService` compara a cidade com o `distribution_center`
   do produto, ignorando acentos e maiúsculas/minúsculas.
   Cidades iguais → `available: true`; diferentes → `false`.

Cada produto pertence a um dos centros de distribuição **Mogi das Cruzes**,
**Recife** ou **Porto Alegre** (definidos na migration `V5`).

### Tratamento de falhas da ViaCEP

| Cenário                                       | Resposta                     |
|-----------------------------------------------|------------------------------|
| CEP mal formatado (`123`, `abcde-fgh`)        | `400 Bad Request`            |
| CEP bem formatado, mas inexistente (`99999999`) | `404 Not Found`            |
| Parâmetro `cep` ausente                       | `400 Bad Request`            |
| Produto inexistente ou inativo                | `404 Not Found`              |
| ViaCEP fora do ar, lenta (timeout) ou sem rede | `503 Service Unavailable`   |

Todas as respostas de erro seguem o formato `{"message": "...", "status": 000}`.
A chamada à ViaCEP tem timeout de conexão (3 s) e de leitura (5 s),
configuráveis em `application.properties`, para a API nunca ficar travada.

Para simular a ViaCEP fora do ar, troque temporariamente a propriedade
`viacep.url` por um endereço inexistente (ex.: `https://viacep.invalid/ws`),
reinicie a aplicação e chame o endpoint: a resposta será `503`.

## Testes no Postman

A collection está em [`postman/Atividade-M1-ViaCEP.postman_collection.json`](postman/Atividade-M1-ViaCEP.postman_collection.json).
Importe no Postman (**Import → arquivo**) e rode a collection inteira
(**Run collection**). Cada requisição já tem testes automáticos que conferem o
status HTTP e, nos casos de disponibilidade, o valor de `available`.

## Estrutura do banco

As migrations ficam em `src/main/resources/db/migration` e são versionadas
pelo Flyway (`V1` a `V5`). A `V5` adiciona a coluna `distribution_center` e
distribui os produtos existentes entre os três centros de distribuição.
