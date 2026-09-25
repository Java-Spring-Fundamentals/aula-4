# integration-viacep

API REST em Java + Spring Boot para cadastro de produtos, com um endpoint de
integração com o serviço [ViaCEP](https://viacep.com.br/) para verificar a
disponibilidade de um produto em determinada cidade a partir de um CEP.

Alyne Rodrigues de Campos  
RGM: 11231100534  
Sistemas de Informação - 8ºB

## Stack

- Java 17
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

## Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/product` | Lista os produtos ativos |
| GET | `/product/{id}` | Busca um produto por id |
| POST | `/product` | Cadastra um produto |
| PUT | `/product/{id}` | Atualiza um produto |
| DELETE | `/product/{id}` | Inativa um produto |
| GET | `/product/category/{category}` | Lista produtos por categoria |
| GET | `/product/top5-by-price` | Retorna os cinco produtos de maior preço |
| GET | `/product/availability/{id}?cep={cep}` | Verifica a disponibilidade pela cidade do CEP |
| POST | `/product/category/{categoryPath}/filter` | Exemplo com Path, Param, Header e Body |
| GET | `/product/cep` | Busca CEP a partir de estado, cidade e rua |

O endpoint de disponibilidade trata CEP inválido, CEP inexistente, produto não
encontrado, timeout e falhas na comunicação com o serviço externo.

## Estrutura do banco

As migrations ficam em `src/main/resources/db/migration` e são versionadas
pelo Flyway (`V1` a `V5`). A tabela `product` tem, entre outros, o campo
`distribution_center`, usado pelo endpoint de disponibilidade para comparar
com a cidade retornada pela ViaCEP.

## Postman

A collection utilizada nos testes está na pasta `postman`.
