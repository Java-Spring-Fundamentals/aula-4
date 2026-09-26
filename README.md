# Atividade M1 — Integração com ViaCEP

API de produtos desenvolvida com Spring Boot. Permite cadastrar, consultar,
atualizar e inativar produtos, além de verificar se a cidade de um CEP
corresponde ao centro de distribuição de um produto.

Projeto desenvolvido a partir da base disponibilizada pelo professor:
https://github.com/Java-Spring-Fundamentals/aula-4

## Tecnologias

- Java 17
- Spring Boot 3.1.0
- Maven Wrapper
- PostgreSQL 15
- Flyway
- Docker Compose
- Postman

## Como executar no Windows

É necessário ter o JDK 17 e o Docker Desktop instalados.

Clone o repositório na branch da atividade:

```powershell
git clone --branch feature/integracao-viacep https://github.com/EduMafra10/atividade-m1-viacep.git
cd atividade-m1-viacep
```

Com o Docker Desktop aberto, inicie o banco:

```powershell
docker compose up -d db
```

Confira se o banco está pronto:

```powershell
docker compose exec db pg_isready -U postgres -d product
```

Quando retornar que está aceitando conexões, execute:

```powershell
.\mvnw.cmd clean spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

O Flyway executa as migrations automaticamente, criando a estrutura
e os dados iniciais. O banco local utiliza a porta 5432, banco `product`,
usuário `postgres` e senha `password`, conforme o Docker Compose.

## Endpoints

| Método | Rota | Função |
|---|---|---|
| GET | `/product` | Listar produtos ativos |
| POST | `/product` | Cadastrar produto |
| PUT | `/product` | Atualizar produto pelo ID enviado no corpo |
| DELETE | `/product/{id}` | Inativar produto |
| GET | `/product/endpoint2/{id}` | Consultar produto por ID |
| GET | `/product/endpoint1?categoryAsParam=electronics` | Filtrar produtos ativos por categoria |
| GET | `/product/endpoint3/top5byprice` | Listar os cinco produtos ativos mais caros |
| GET | `/product/availability/{id}?cep=08773380` | Consultar disponibilidade pela cidade do CEP |
| GET | `/product/category/{categoryAsPath}` | Consultar categoria usando quatro formas de entrada |
| GET | `/product/cep?state=SP&city=Mogi%20das%20Cruzes&street=Rua%20Francisco%20Franco` | Buscar CEP por endereço |

A inativação mantém o registro no banco com `active: false`.
O produto continua acessível pela consulta por ID, mas deixa de aparecer
na listagem de produtos ativos.

## Cadastro e atualização

Exemplo de corpo JSON para cadastrar:

```json
{
  "name": "Teclado",
  "price": 15000,
  "category": "electronics",
  "distributionCenter": "Mogi das Cruzes"
}
```

O cadastro gera o ID automaticamente e retorna HTTP 201.

Para atualizar, envie os mesmos campos usando PUT e acrescente o campo
`id` com o identificador retornado no cadastro. A atualização permite
alterar nome, preço, categoria e centro de distribuição.

Nome e categoria são obrigatórios, e o preço deve ser um inteiro positivo.
Os centros aceitos são Mogi das Cruzes, Recife e Porto Alegre.

## Integração com ViaCEP

O serviço consulta a ViaCEP pelo CEP informado e compara `localidade`
com o centro de distribuição do produto, ignorando diferenças entre
letras maiúsculas e minúsculas e espaços nas extremidades.

- Cidades iguais: HTTP 200 com `true`.
- Cidades diferentes: HTTP 200 com `false`.
- CEP fora do formato de oito números: HTTP 400.
- CEP ou produto inexistente: HTTP 404.
- Falha de comunicação com a ViaCEP: HTTP 503.

O cliente HTTP possui limite de 3 segundos para conexão e de 5 segundos
para leitura da resposta.

## Testes com Postman

Importe o arquivo:

`postman/atividade-m1-viacep.postman_collection.json`

A coleção contém 17 requisições utilizadas nos testes manuais de CRUD,
consultas, validações e integração.

Para repetir o CRUD em um banco novo:

1. Execute a requisição de cadastro.
2. Copie o ID retornado.
3. Substitua o ID no corpo da requisição de atualização.
4. Execute a atualização e consulte o produto por ID.
5. Substitua o ID na URL da requisição de inativação e execute.
6. Confira `active: false` na consulta por ID e a ausência na lista de ativos.

Os IDs salvos nas requisições de atualização e inativação pertencem
ao produto criado durante o teste original e devem ser substituídos.

Para a consulta com quatro entradas, use:

- Caminho: `/product/category/electronics`
- Parâmetro: `categoryAsParam=electronics`
- Cabeçalho: `categoryAsHeader: electronics`
- Corpo JSON: `{"category": "electronics"}`

Essa rota mantém o exemplo didático da aula de GET com corpo.
As quatro entradas devem informar a mesma categoria.

## Teste de indisponibilidade

Pare a aplicação com Ctrl + C e inicie com:

```powershell
.\mvnw.cmd clean spring-boot:run "-Dspring-boot.run.arguments=--viacep.base-url=http://127.0.0.1:1"
```

Esse endereço simula uma falha de conexão com o serviço externo.

Execute a requisição de indisponibilidade da coleção: o esperado é
HTTP 503 com uma mensagem de erro. A listagem de produtos deve continuar
retornando HTTP 200.

Esse teste exige a configuração especial e deve ser executado separadamente
dos testes normais da ViaCEP.

Depois, pare a aplicação e reinicie normalmente:

```powershell
.\mvnw.cmd spring-boot:run
```

## Encerramento

Pare a aplicação com Ctrl + C. Para parar o banco sem remover o container:

```powershell
docker compose stop db
```