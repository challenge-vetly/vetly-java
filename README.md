# Vetly — Backend API (Java)

Vetly é uma plataforma de gestão veterinária que conecta tutores, veterinários e clínicas por meio de uma experiência digital unificada. Este repositório contém o serviço backend construído com **Java 21** e **Spring Boot**, responsável pelo modelo de domínio, persistência de dados e camada de API REST.

---

## Índice

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Modelo de Domínio](#modelo-de-domínio)
- [API REST — Endpoints Implementados](#api-rest--endpoints-implementados)
- [Segurança e Autenticação](#segurança-e-autenticação)
- [Como Executar](#como-executar)
- [Configuração](#configuração)
- [Documentação Swagger](#documentação-swagger)
- [Roadmap](#roadmap)

---

## Visão Geral

O backend Vetly implementa as entidades centrais do domínio e uma camada de API REST funcional para os primeiros módulos operacionais:

- **Autenticação** com JWT — login, registro de veterinários e administradores
- **Gestão de Usuários** — CRUD paginado com controle de papéis (ADMIN / VETERINÁRIO / TUTOR)
- **Espécies** — cadastro e listagem das espécies de animais atendidas
- **Especialidades Veterinárias** — cadastro, listagem paginada com links HATEOAS e busca por filtros (`nome`, `descricao`)
- **Modelo de domínio completo** — entidades mapeadas com JPA para: Animal, Consulta, Prontuário, Evolução Clínica, Solicitação de Exame, Tutor, Veterinário e demais tabelas auxiliares

---

## Stack Tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Persistência | Spring Data JPA (Hibernate) |
| Banco de Dados | Oracle Database (ojdbc11) |
| Segurança | Spring Security + JWT (`com.auth0:java-jwt:4.4.0`) |
| API | Spring Web MVC + Spring HATEOAS |
| Validação | Bean Validation (Jakarta) |
| Build | Gradle |
| Testes | JUnit 5 via Spring Boot Test |

---

## Estrutura do Projeto

```
vetly-java/
├── build.gradle
├── settings.gradle
├── swagger.yaml                        # Documentação OpenAPI 3.0
└── src/
    └── main/
        ├── java/com/petly/petly_java/
        │   ├── PetlyJavaApplication.java
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── EspecialidadeVetController.java
        │   │   ├── EspecieController.java
        │   │   └── UsuarioController.java
        │   ├── dto/
        │   │   ├── AuthDTO.java
        │   │   ├── EspecialidadeVetLista.java
        │   │   ├── EspecialidadeVetRequest.java
        │   │   ├── EspecieRequest.java
        │   │   ├── LoginResponseDTO.java
        │   │   ├── RegisterAdminDTO.java
        │   │   ├── RegisterVeterinarioDTO.java
        │   │   ├── UsuarioDTO.java
        │   │   ├── UsuarioLista.java
        │   │   └── UsuarioResponse.java
        │   ├── exception/
        │   │   └── GlobalExceptionHandler.java
        │   ├── mapper/
        │   │   ├── EspecialidadeVetMapper.java
        │   │   ├── EspecieMapper.java
        │   │   └── UsuarioMapper.java
        │   ├── model/
        │   │   ├── Animal.java
        │   │   ├── AnexoExame.java
        │   │   ├── Consulta.java
        │   │   ├── Disponibilidade.java         # ⚠️ Em desenvolvimento
        │   │   ├── EspecialidadeVet.java
        │   │   ├── Especie.java
        │   │   ├── EvolucaoClinica.java
        │   │   ├── LogErro.java
        │   │   ├── NomeEspecialidade.java        # Enum (21 especialidades)
        │   │   ├── NomeEspecie.java              # Enum (8 espécies)
        │   │   ├── Pessoa.java
        │   │   ├── Prontuario.java
        │   │   ├── Sexo.java
        │   │   ├── SolicitacaoExame.java
        │   │   ├── SolicitacaoExameItem.java
        │   │   ├── StatusConsulta.java
        │   │   ├── StatusExame.java
        │   │   ├── Tutor.java
        │   │   ├── UserRole.java
        │   │   ├── Usuario.java
        │   │   ├── Veterinario.java
        │   │   ├── VeterinarioEspecialidade.java
        │   │   └── VeterinarioEspecie.java
        │   ├── repository/
        │   │   ├── EspecialidadeVetRepository.java
        │   │   ├── EspecieRepository.java
        │   │   ├── PessoaRepository.java
        │   │   ├── UsuarioRepository.java
        │   │   ├── VeterinarioEspecialidadeRepository.java
        │   │   ├── VeterinarioEspecieRepository.java
        │   │   └── VeterinarioRepository.java
        │   ├── security/
        │   │   ├── SecurityConfigurations.java
        │   │   └── SecurityFilter.java
        │   ├── service/
        │   │   ├── AuthService.java
        │   │   ├── EspecialidadeVetService.java
        │   │   ├── EspecieService.java
        │   │   ├── TokenService.java
        │   │   └── UsuarioService.java
        │   └── validation/
        │       ├── ValueOfEnum.java
        │       ├── ValueOfEnumValidator.java
        │       └── ValueOfEnumListValidator.java
        └── resources/
            └── application.properties
```

---

## Modelo de Domínio

### Entidades Persistidas

#### `Usuario`
Conta de acesso ao sistema. Implementa `UserDetails` para integração com Spring Security.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador único |
| `email` | String | E-mail único de login |
| `role` | UserRole (enum) | Papel: `ADMIN`, `TUTOR` ou `VETERINARIO` |
| `flagAtivo` | String (`S`/`N`) | Status ativo/inativo |
| `senhaHash` | String | Senha criptografada com BCrypt |

#### `Pessoa`
Dados pessoais compartilhados por Veterinário e Tutor.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador único |
| `nome` | String (100) | Nome completo |
| `cpf` | String (11) | CPF (único junto ao telefone) |
| `telefone` | String (15) | Telefone de contato |

#### `Veterinario`
Profissional veterinário vinculado a um `Usuario` e uma `Pessoa`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador único |
| `crmv` | String (20) | Número único do CRMV |
| `usuario` | Usuario | Conta de acesso (1:1) |
| `pessoa` | Pessoa | Dados pessoais (1:1) |
| `especialidades` | List\<VeterinarioEspecialidade\> | Especialidades do vet |
| `especies` | List\<VeterinarioEspecie\> | Espécies que atende |

#### `Tutor`
Dono do animal, vinculado a um `Usuario` e uma `Pessoa`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `usuario` | Usuario | Conta de acesso (1:1) |
| `pessoa` | Pessoa | Dados pessoais (1:1) |

#### `Animal`
Animal cadastrado na plataforma, associado a um `Tutor` e uma `Especie`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `nome` | String (80) | Nome do animal |
| `raca` | String (80) | Raça |
| `sexo` | Sexo (enum) | `M` ou `F` |
| `dataNascimento` | LocalDate | Data de nascimento |
| `peso` | BigDecimal | Peso em kg |
| `tutor` | Tutor | Tutor responsável |
| `especie` | Especie | Espécie do animal |

#### `EspecialidadeVet`
Especialidade veterinária cadastrada no sistema (tabela de domínio).

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador único |
| `nome` | NomeEspecialidade (enum) | Nome único da especialidade |
| `descricao` | String (150) | Descrição da especialidade |

#### `Especie`
Espécie animal (tabela de domínio).

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador único |
| `nome` | NomeEspecie (enum) | Nome único da espécie |

#### `Consulta`
Agendamento entre veterinário e animal.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `dataHora` | LocalDateTime | Data e hora da consulta |
| `status` | StatusConsulta (enum) | `AGENDADA`, `CANCELADA` ou `REALIZADA` |
| `valor` | BigDecimal | Valor cobrado |
| `observacao` | String (500) | Observações opcionais |
| `veterinario` | Veterinario | Veterinário responsável |
| `animal` | Animal | Animal atendido |

#### `Prontuario`
Histórico clínico longitudinal do animal (1:1 com Animal).

#### `EvolucaoClinica`
Anotações clínicas produzidas em uma consulta (1:1 com Consulta).

#### `SolicitacaoExame` / `SolicitacaoExameItem` / `AnexoExame`
Fluxo completo de solicitação, acompanhamento e resultado de exames laboratoriais.

#### `LogErro`
Registro de erros de execução para auditoria.

---

### Enums

#### `NomeEspecialidade` — 21 especialidades veterinárias
`ACUPUNTURA`, `ANESTESIOLOGIA`, `CARDIOLOGIA`, `CIRURGIA`, `DERMATOLOGIA`, `ENDOCRINOLOGIA`, `FISIOTERAPIA`, `GERIATRIA`, `HOMEOPATIA`, `MEDICINA_ANIMAIS_SELVAGENS`, `MEDICINA_DO_COLETIVO`, `MEDICINA_INTENSIVA`, `NEFROLOGIA_E_UROLOGIA`, `NEUROLOGIA`, `NUTROLOGIA`, `ODONTOLOGIA`, `OFTALMOLOGIA`, `PATOLOGIA`, `PEQUENOS_ANIMAIS`, `PNEUMOLOGIA`, `RADIOLOGIA`

#### `NomeEspecie` — 8 espécies
`AVE`, `REPTIL`, `MAMIFERO`, `ANFIBIO`, `PEIXE`, `EQUIDEO`, `CAO`, `GATO`

#### `UserRole` — 3 papéis
`ADMIN`, `TUTOR`, `VETERINARIO`

#### `StatusConsulta`
`AGENDADA`, `CANCELADA`, `REALIZADA`

#### `StatusExame`
`SOLICITADO`, `AGUARDANDO_RESULTADO`, `RESULTADO_ENVIADO`, `ANALISADO`, `CANCELADO`

---

### Diagrama de Relacionamentos

```
Usuario ────────────────────────┐
  ├── (1:1) Veterinario          │
  │     ├── (1:1) Pessoa         │
  │     ├── (1:N) VeterinarioEspecialidade → EspecialidadeVet
  │     └── (1:N) VeterinarioEspecie      → Especie
  └── (1:1) Tutor                │
        └── (1:1) Pessoa         │
                                 │
Animal ──────────────────────────┘
  ├── (N:1) Tutor
  ├── (N:1) Especie
  └── (1:1) Prontuario
              └── (indireta via Consulta) EvolucaoClinica

Consulta
  ├── (N:1) Veterinario
  ├── (N:1) Animal
  ├── (1:1) EvolucaoClinica
  └── (1:1) SolicitacaoExame
              └── (1:N) SolicitacaoExameItem
                            └── (1:N) AnexoExame
```

---

## API REST — Endpoints Implementados

### Autenticação (`/auth`)

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/auth/login` | Público | Autentica e retorna JWT |
| `POST` | `/auth/register/veterinario` | Público | Registra veterinário com perfil completo |
| `POST` | `/auth/register/admin` | Público | Registra administrador |

### Usuários (`/usuarios`)

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/usuarios` | ADMIN | Cria usuário |
| `GET` | `/usuarios` | ADMIN | Lista paginada (5/pág, ordenado por e-mail) |
| `GET` | `/usuarios/{id}` | ADMIN | Busca por ID |

### Espécies (`/especies`)

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/especies` | ADMIN | Cria espécie |
| `GET` | `/especies` | Público | Lista todas as espécies |
| `GET` | `/especies/{id}` | Público | Busca por ID |
| `PUT` | `/especies` | ADMIN | Atualiza espécie (ID no corpo) |
| `DELETE` | `/especies/{id}` | ADMIN | Remove espécie |

### Especialidades Veterinárias (`/especialidades`)

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `POST` | `/especialidades` | ADMIN | Cria especialidade |
| `GET` | `/especialidades` | Público | Lista paginada (2/pág, ordenado por nome) com HATEOAS e filtros opcionais |
| `GET` | `/especialidades/{id}` | Público | Busca por ID |
| `PUT` | `/especialidades` | ADMIN | Atualiza especialidade (ID no corpo) |
| `DELETE` | `/especialidades/{id}` | ADMIN | Remove especialidade |

#### Parâmetros de busca — `GET /especialidades`

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `pageNumber` | integer | não (padrão: `0`) | Página (base 0, 2 registros por página) |
| `nome` | string | não | Trecho do nome da especialidade — case-insensitive |
| `descricao` | string | não | Trecho da descrição — case-insensitive |

Os filtros são combináveis. A busca usa JPQL com `LIKE` sobre os dois campos simultaneamente.

```
GET /especialidades                            → lista todos
GET /especialidades?nome=cardio                → nome contém "cardio"
GET /especialidades?descricao=coracao          → descrição contém "coracao"
GET /especialidades?nome=cardio&descricao=animal → ambos os filtros
GET /especialidades?nome=cardio&pageNumber=1   → filtro + segunda página
```

---

## Segurança e Autenticação

A API utiliza **JWT stateless** com Spring Security:

- Tokens gerados pela `TokenService` usando a biblioteca `com.auth0:java-jwt`
- O `SecurityFilter` intercepta cada requisição, extrai e valida o token, e popula o contexto de segurança
- Senhas armazenadas com **BCryptPasswordEncoder**
- Sessões HTTP são stateless (`SessionCreationPolicy.STATELESS`)
- CSRF desabilitado (API REST sem estado)

### Regras de acesso

| Recurso | Método | Papel exigido |
|---------|--------|---------------|
| `/auth/**` | POST | Público |
| `/especies`, `/especialidades` | GET | Público |
| `/especies`, `/especialidades`, `/usuarios` | POST, PUT, DELETE | ADMIN |
| Demais endpoints | Qualquer | Autenticado |

---

## Como Executar

### Pré-requisitos

- Java 21+
- Gradle 8+
- Instância Oracle Database acessível

### Executando a aplicação

```bash
# Clonar o repositório
git clone https://github.com/your-org/vetly-java.git
cd vetly-java

# Build
./gradlew build

# Executar
./gradlew bootRun
```

### Executando os testes

```bash
./gradlew test
```

---

## Configuração

O arquivo `src/main/resources/application.properties` deve ser configurado antes de executar:

```properties
spring.application.name=petly-java

# Segurança JWT — troque em produção!
api.security.token.secret=sua-chave-secreta-forte

# Conexão Oracle
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/orcl
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração para respostas 404 em rotas inexistentes
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```
### Banco de dados
O arquivo `DDL.txt` possui o codigo para criacao das tabelas no Oracle Database.
Rode os comandos e substitua as credenciais no `src/main/resources/application.properties`
para testar a persistencia dos dados.

> ⚠️ **Atenção:** nunca comite credenciais reais no repositório. Use variáveis de ambiente ou um cofre de segredos em produção.

---

## Documentação Swagger

A documentação completa da API está disponível no arquivo `swagger.yaml` (OpenAPI 3.0) na raiz do projeto.

**Swagger UI:**

Adicione ao `build.gradle` para servir o Swagger UI diretamente pela aplicação:
```groovy
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
```
Depois acesse: `http://localhost:8080/swagger-ui.html`

---

## Roadmap

### Em desenvolvimento
- [ ] `Disponibilidade` — modelo de grade horária do veterinário (entidade criada, sem campos)
- [ ] Registro de Tutor (`/auth/register/tutor`) — código comentado em `AuthService`

### Próximas funcionalidades

**Camada de Dados**
- [ ] Repositórios JPA para: `Animal`, `Tutor`, `Consulta`, `Prontuario`, `EvolucaoClinica`, `SolicitacaoExame`
- [ ] Modelo financeiro (pagamentos, split de receita, assinaturas)

**Camada de Aplicação**
- [ ] Services para Animal, Consulta, Prontuário, Exames
- [ ] Serviço de validação de CRMV (integração com conselhos regionais)
- [ ] Resolução de conflitos de disponibilidade na agenda

**Camada de API**
- [ ] Controllers REST para Animal, Consulta, Prontuário, Exames
- [ ] Integração do `springdoc-openapi` para servir Swagger UI in-app

**Integrações**
- [ ] Abacate Pay — processamento de pagamentos e split financeiro
- [ ] WhatsApp — comunicações proativas e fluxos conversacionais
- [ ] Assistente IA — sugestões diagnósticas e geração de protocolos

**Infraestrutura**
- [ ] Rastreamento de consentimento LGPD
- [ ] Geração de documentos (prontuário, receita, atestado, nota fiscal)
- [ ] Log de auditoria para correções em prontuários
