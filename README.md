# Vetly — Backend API (Java)

Backend em **Java 21 + Spring Boot** da plataforma Vetly, um marketplace que conecta
tutores a veterinários independentes. Este serviço cobre a fatia de domínio
**Animal · Consulta · Prontuário · Colmeia · Exame**, além de autenticação e dos
cadastros de apoio (Usuário, Espécie, Especialidade, Veterinário).

> **Contexto de projeto.** O Vetly tem dois backends. O `vetly-.net` implementa o
> produto completo; este repositório é uma implementação **paralela e independente**
> de um subconjunto do domínio — identidade, banco e dados próprios, **sem integração**
> com o `.NET`. As regras de negócio seguidas aqui são as **atuais** (RN-001 a RN-107 de
> `vetly-qa/vetly-tech.md`), não as do produto legado.

---

## Índice

- [Escopo do domínio](#escopo-do-domínio)
- [Stack tecnológica](#stack-tecnológica)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Regras de negócio implementadas](#regras-de-negócio-implementadas)
- [Modelo de domínio](#modelo-de-domínio)
- [API REST — endpoints](#api-rest--endpoints)
- [Segurança e autenticação](#segurança-e-autenticação)
- [Banco de dados e migrations](#banco-de-dados-e-migrations)
- [Como executar](#como-executar)
- [Configuração](#configuração)
- [Documentação Swagger e roteiro de teste](#documentação-swagger-e-roteiro-de-teste)
- [Roadmap](#roadmap)

---

## Escopo do domínio

| Dentro deste repositório | Somente no `vetly-.net` |
|---|---|
| Auth própria (tutor, veterinário, admin) | Empresa / Administrador |
| Animal — CRUD com campos clínicos | Pagamento, split financeiro, fidelidade |
| Consulta — agendamento e máquina de estados | Matching e geolocalização |
| Prontuário — versionamento original × correção | IA na consulta (áudio, sugestões) |
| Colmeia — acesso ao histórico + log de acessos | Internação |
| Exame — solicitação, resultado, anexo, liberação | Notificações push, avaliação, avatar |
| Consentimento LGPD/rede do tutor (versão mínima) | Documentos além do prontuário (receita, atestado, NF) |

Decisões de fronteira e a justificativa de cada exclusão estão em
`project-context/docs/java-scope.md`.

---

## Stack tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Persistência | Spring Data JPA (Hibernate) |
| Migrations | Flyway (`flyway-core` + `flyway-database-oracle`) |
| Banco de dados | Oracle Database (ojdbc11) |
| Segurança | Spring Security + JWT (`com.auth0:java-jwt:4.4.0`) |
| API | Spring Web MVC + Spring HATEOAS |
| Documentação | springdoc-openapi 3.0.2 (Swagger UI embutido) |
| Validação | Bean Validation (Jakarta) + Hibernate Validator (`@CPF`) |
| Build | Gradle |
| Testes | JUnit 5 via Spring Boot Test |

---

## Estrutura do projeto

```
vetly-java/
├── build.gradle
├── settings.gradle
├── DDL.txt                                  # Schema Oracle completo (espelha vetly-database)
├── swagger.yaml                             # OpenAPI 3.0 escrito à mão, com mocks de teste
└── src/main/
    ├── java/com/vetly/vetly_java/
    │   ├── VetlyJavaApplication.java
    │   ├── controller/
    │   │   ├── AuthController.java           # /auth
    │   │   ├── UsuarioController.java        # /usuarios
    │   │   ├── EspecieController.java        # /especies
    │   │   ├── EspecialidadeVetController.java  # /especialidades
    │   │   ├── VeterinarioController.java    # /veterinarios
    │   │   ├── TutorController.java          # /tutores/me/consentimento
    │   │   ├── AnimalController.java         # /animais
    │   │   ├── ConsultaController.java       # /consultas
    │   │   ├── ColmeiaController.java        # /animais/{id}/historico, /logs-acesso
    │   │   ├── ProntuarioController.java     # /animais/{id}/prontuario
    │   │   ├── SolicitacaoExameController.java  # /consultas/{id}/solicitacoes-exame, /solicitacoes-exame/itens/**
    │   │   └── OpenApiDocController.java      # serve o swagger.yaml em /swagger.yaml
    │   ├── dto/                              # Records de request/response (um por caso de uso)
    │   ├── exception/GlobalExceptionHandler.java
    │   ├── mapper/                           # Entidade ↔ DTO (inclui a máscara RN-104 do exame)
    │   ├── model/                            # Entidades JPA e enums
    │   ├── repository/                       # Spring Data JPA
    │   ├── security/                         # SecurityConfigurations, SecurityFilter
    │   ├── service/                          # Regras de negócio (ver seção abaixo)
    │   └── validation/                       # @ValueOfEnum
    └── resources/
        ├── application.properties
        └── db/migration/                     # V1..V8 (Flyway)
```

Onde cada regra mora:

| Service | Responsabilidade |
|---|---|
| `AuthService` | Registro transacional de tutor e veterinário |
| `AnimalService` | CRUD do animal escopado ao tutor dono |
| `ConsultaService` | Agendamento e transições de estado |
| `AcessoProntuarioService` | Colmeia: vínculo clínico, escopo por consentimento e log de acesso |
| `ProntuarioService` | Criação e correção versionada do prontuário |
| `SolicitacaoExameService` | Ciclo do exame e liberação ao Responsável |
| `TutorService` | Consentimento LGPD e de rede |

---

## Regras de negócio implementadas

Numeração conforme `vetly-qa/vetly-tech.md` — **única fonte de verdade**. A tabela abaixo
lista o que está de fato implementado neste serviço e onde verificar.

| RN | Regra | Como está implementada | Onde |
|----|-------|------------------------|------|
| **RN-003** | Consulta atribuída ao profissional escolhido | O tutor informa `veterinarioId` no agendamento; não há clínica designando (Empresa está fora de escopo) | `POST /consultas` |
| **RN-008** | Consulta encerra por ação do vet | Só o veterinário designado marca `REALIZADA`; sem IA acoplada, isso apenas fecha o registro | `PATCH /consultas/{id}/realizar` |
| **RN-012** | Responsável cancela ou remarca | Ambas as ações são exclusivas do tutor dono do animal | `PATCH /consultas/{id}/cancelar`, `/reagendar` |
| **RN-038** | Estados da consulta | `AGENDADA → REALIZADA \| CANCELADA \| NAO_COMPARECEU`; toda transição parte de `AGENDADA`, o resto responde `409` | `ConsultaService.transicionar` |
| **RN-060** | Consentimento LGPD registrado | Aceite com carimbo de data no momento em que passa a `true` | `PUT /tutores/me/consentimento` |
| **RN-062** | Revogação no app, com registro | `consentimentoRede: false` limpa a data e passa a valer nos acessos seguintes, sem apagar registro clínico já produzido | `TutorService.atualizarConsentimento` |
| **RN-063** | Prontuário pertence ao animal | A rota é `/animais/{animalId}/prontuario`; nenhum vet "possui" o prontuário | `ProntuarioController` |
| **RN-064** | Colmeia por evento clínico | Ter ao menos uma consulta **não cancelada** com o animal concede acesso automático ao histórico | `AcessoProntuarioService.exigirVinculoClinico` |
| **RN-066** | Sem consentimento, acesso restrito | Com `consentimentoRede = false`, o vet só enxerga as evoluções que ele próprio produziu (`baseAcesso: ATENDIMENTO_DIRETO`) | `AcessoProntuarioService.historicoParaVeterinario` |
| **RN-067** | Log de acesso visível ao Responsável | Todo acesso de vet grava quem/quando/contexto/base legal; o tutor lê a trilha | `GET /animais/{id}/logs-acesso` |
| **RN-068** | Alertas de segurança nunca ocultáveis | O filtro do histórico do tutor deixa passar todo registro com `alertaSeguranca = true`, mesmo marcado como oculto | `AcessoProntuarioService.historicoParaTutor` |
| **RN-081** | Peso obrigatório | `peso` é `@NotNull` e maior que zero no cadastro e na atualização do animal | `AnimalRequest` |
| **RN-088** | Correção vinculada ao original | A correção cria **nova linha** apontando para a original, com data, hora e CRMV; o original nunca é sobrescrito e as duas versões voltam na leitura | `ProntuarioService.corrigir` |
| **RN-089** | Correção fora de 24h exige justificativa | Passada a janela, `justificativa` em branco responde `409` | `ProntuarioService.corrigir` |
| **RN-103** | Exame solicitado na plataforma | O vet da consulta cria uma solicitação com N itens, vinculada à consulta e, por ela, ao animal | `POST /consultas/{id}/solicitacoes-exame` |
| **RN-104** | Resultado só chega ao Responsável após liberação | Registrar o laudo (`ANALISADO`) não revela nada; só `PATCH .../liberar` torna resultado e anexos visíveis ao tutor | `SolicitacaoExameMapper.toItemResponse` |

### Adaptações e exclusões conscientes

| RN | Situação neste serviço |
|----|------------------------|
| RN-013 (pagamento transferido na remarcação) | **Não se aplica** — não há Pagamento no Java; remarcar só troca a data |
| RN-035 (`EM_CHECKOUT`) | **Removido do ciclo** — o estado existe por causa do lock de pagamento |
| RN-041 a RN-045 (cancelamento com reembolso) | **Fora de escopo** — pressupõem Pagamento |
| RN-046 (obrigações do pet) | **Fora do v1** — é consumida por Fidelidade/Avatar/Notificações, que são `.NET` |
| RN-061 (consentimento granular por finalidade) | **Versão mínima** — dois flags (LGPD e rede) em vez das cinco finalidades; as demais só fazem sentido com Fidelidade/Monetização |
| RN-065 (expiração do acesso da Colmeia) | **Não implementada** — o vínculo clínico hoje não expira ao fim do ciclo; cancelar a consulta é o único jeito de retirá-lo |
| RN-078 a RN-085 (IA na consulta) | **Somente `.NET`** — sem captura de áudio ou sugestão diagnóstica aqui |
| RN-100 a RN-102 (internação) | **Somente `.NET`** |
| RN-107 (validação de CRMV) | **Não implementada** — só é relevante com matching/diretório público de vets |

### Decisão de implementação registrada

`StatusExame.RESULTADO_ENVIADO` significa **liberado ao Responsável**, não "enviado ao
veterinário". A ambiguidade estava aberta em `docs/java-database-alignment.md` §2.7 e foi
resolvida assim porque é a leitura que corresponde a RN-104: o marco que importa é a
divulgação ao tutor.

---

## Modelo de domínio

### Entidades

#### `Usuario`
Conta de acesso; implementa `UserDetails`.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | String (UUID) | Identificador |
| `email` | String | E-mail único de login |
| `role` | UserRole | `ADMIN`, `TUTOR` ou `VETERINARIO` |
| `flagAtivo` | String (`S`/`N`) | Conta ativa |
| `senhaHash` | String | BCrypt |

#### `Pessoa`
Dados pessoais compartilhados por Veterinário e Tutor — `nome`, `cpf`, `telefone`
(CPF+telefone únicos em conjunto).

#### `Tutor`
Dono do animal. Além do vínculo com `Usuario` e `Pessoa`, carrega o consentimento:

| Campo | Tipo | RN |
|-------|------|----|
| `lgpdAceito` / `dataLgpdAceito` | `S`/`N` + LocalDate | RN-060 |
| `consentimentoRede` / `dataConsentimentoRede` | `S`/`N` + LocalDate | RN-064 / RN-066 / RN-062 |

#### `Veterinario`
`crmv` único, `usuario`, `pessoa`, mais as listas `especialidades` e `especies`.

#### `Animal`
Pertence a um `Tutor` e a uma `Especie`.

| Campo | Tipo | Observação |
|-------|------|-----------|
| `id` | UUID | |
| `nome`, `raca` | String (80) | |
| `sexo` | Sexo | `M` / `F` |
| `dataNascimento` | LocalDate | Não pode ser futura |
| `peso` | BigDecimal | **Obrigatório** (RN-081) |
| `urlFoto` | String (500) | |
| `castrado` | `S`/`N` | |
| `condicoesPreexistentes`, `alergias`, `medicacoesEmUso` | String (1000) | `alergias` alimenta alertas de segurança (RN-068) |

#### `Consulta`
`dataHora`, `status` (`StatusConsulta`), `valor`, `observacao`, `veterinario`, `animal`.
É o vínculo clínico que a Colmeia consulta (RN-064).

#### `Prontuario`
Uma linha por versão do prontuário do animal.

| Campo | Descrição |
|-------|-----------|
| `conteudoClinico` | Texto clínico da versão |
| `dataUltimaAtualizacao` | Data da versão |
| `original` | Auto-FK: `null` na original, aponta para ela em cada correção (RN-088) |
| `dataHoraCorrecao`, `crmvSolicitanteCorrecao` | Carimbo da correção (RN-088) |
| `justificativaCorrecao` | Preenchida só fora da janela de 24h (RN-089) |

#### `EvolucaoClinica`
Anotação produzida em uma consulta (1:1 com `Consulta`) — é o "histórico" que a Colmeia
serve. Tem `ocultoResponsavel` e `alertaSeguranca` (RN-068).

#### `LogAcessoProntuario`
Trilha da Colmeia (RN-067): `dataHoraAcesso`, `contextoAcesso`, `baseAcesso`, `animal`, `veterinario`.

#### `SolicitacaoExame` / `SolicitacaoExameItem` / `AnexoExame`
Uma solicitação por consulta, N itens por solicitação, N anexos por item.
O item guarda `status`, `descricaoResultado`, `liberadoResponsavel` e
`dataLiberacaoResponsavel` (RN-104).

#### `LogErro`
Registro de erros de execução para auditoria.

### Enums

| Enum | Valores |
|------|---------|
| `UserRole` | `ADMIN`, `TUTOR`, `VETERINARIO` |
| `Sexo` | `M`, `F` |
| `StatusConsulta` | `AGENDADA`, `REALIZADA`, `CANCELADA`, `NAO_COMPARECEU` |
| `StatusExame` | `SOLICITADO`, `AGUARDANDO_RESULTADO`, `ANALISADO`, `RESULTADO_ENVIADO`, `CANCELADO` |
| `BaseAcesso` | `CONSENTIMENTO_REDE`, `ATENDIMENTO_DIRETO` |
| `NomeEspecie` | `AVE`, `REPTIL`, `MAMIFERO`, `ANFIBIO`, `PEIXE`, `EQUIDEO`, `CAO`, `GATO` |
| `NomeEspecialidade` | 21 especialidades (`ACUPUNTURA` … `RADIOLOGIA`) |

### Relacionamentos

```
Usuario
  ├── (1:1) Veterinario ── (1:1) Pessoa
  │           ├── (1:N) VeterinarioEspecialidade → EspecialidadeVet
  │           └── (1:N) VeterinarioEspecie       → Especie
  └── (1:1) Tutor ─────── (1:1) Pessoa
                └── (1:N) Animal
                            ├── (N:1) Especie
                            ├── (1:N) Prontuario           # original + correções (RN-088)
                            └── (1:N) LogAcessoProntuario  # RN-067

Consulta
  ├── (N:1) Veterinario          # vínculo clínico da Colmeia (RN-064)
  ├── (N:1) Animal
  ├── (1:1) EvolucaoClinica      # histórico servido pela Colmeia
  └── (1:1) SolicitacaoExame
              └── (1:N) SolicitacaoExameItem
                            └── (1:N) AnexoExame
```

### Máquina de estados

```
Consulta (RN-038)                    Item de exame (RN-103/104)

        AGENDADA                          SOLICITADO
       /    |    \                            |  registrar resultado (vet)
      /     |     \                           v
CANCELADA REALIZADA NAO_COMPARECEU        ANALISADO
 (tutor)   (vet)      (vet)                   |  liberar (vet)
                                              v
                                      RESULTADO_ENVIADO   # = liberado ao tutor
```

Qualquer transição que não parta do estado de origem responde `409 Conflict`.

---

## API REST — endpoints

### Autenticação — `/auth` (público)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/auth/login` | Autentica e devolve o JWT |
| `POST` | `/auth/register/tutor` | Auto-registro de tutor |
| `POST` | `/auth/register/veterinario` | Auto-registro de veterinário com especialidades e espécies |
| `POST` | `/auth/register/admin` | Cria conta `ADMIN` |

### Usuários — `/usuarios` (ADMIN)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/usuarios` | Cria conta de qualquer papel |
| `GET` | `/usuarios` | Lista paginada (5/pág, por e-mail) com HATEOAS |
| `GET` | `/usuarios/{id}` | Busca por ID |

### Espécies — `/especies`

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `GET` | `/especies` | Público | Lista completa (sem paginação) |
| `GET` | `/especies/{id}` | Público | Busca por ID |
| `POST` | `/especies` | ADMIN | Cria |
| `PUT` | `/especies` | ADMIN | Atualiza — **ID no corpo** |
| `DELETE` | `/especies/{id}` | ADMIN | Remove |

### Especialidades — `/especialidades`

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| `GET` | `/especialidades` | Público | Lista paginada (2/pág, por nome) com HATEOAS |
| `GET` | `/especialidades/{id}` | Público | Busca por ID (`204` se não achar) |
| `POST` | `/especialidades` | ADMIN | Cria |
| `PUT` | `/especialidades` | ADMIN | Atualiza — **ID no corpo** |
| `DELETE` | `/especialidades/{id}` | ADMIN | Remove |

O único parâmetro de `GET /especialidades` é `pageNumber` — para achar profissionais por
especialidade use `GET /veterinarios?especialidade=`.

### Veterinários — `/veterinarios` (público)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/veterinarios` | Lista paginada (5/pág, por nome); `?especialidade=CARDIOLOGIA` filtra |
| `GET` | `/veterinarios/{id}` | Perfil com CRMV e especialidades |

### Tutor — `/tutores/me` (TUTOR)

| Método | Endpoint | RN | Descrição |
|--------|----------|----|-----------|
| `GET` | `/tutores/me/consentimento` | RN-060 | Estado atual do consentimento |
| `PUT` | `/tutores/me/consentimento` | RN-060/062/064 | Registra aceite LGPD e liga/desliga o compartilhamento em rede |

### Animais — `/animais` (TUTOR dono)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/animais` | Cadastra animal do tutor autenticado (RN-081: peso obrigatório) |
| `GET` | `/animais` | Lista paginada (10/pág, por nome) dos próprios animais |
| `GET` | `/animais/{id}` | Busca por ID — `403` se for de outro tutor |
| `PUT` | `/animais/{id}` | Atualização completa |
| `DELETE` | `/animais/{id}` | Remove (`204`) |

### Consultas — `/consultas`

| Método | Endpoint | Quem | Descrição |
|--------|----------|------|-----------|
| `POST` | `/consultas` | TUTOR | Agenda com o vet escolhido (RN-003) |
| `GET` | `/consultas` | TUTOR / VET | Página de 10, recortada pelo papel |
| `GET` | `/consultas/{id}` | TUTOR dono / VET designado | Detalhe |
| `PATCH` | `/consultas/{id}/reagendar` | TUTOR | Nova data (RN-012) |
| `PATCH` | `/consultas/{id}/cancelar` | TUTOR | `→ CANCELADA` (RN-012) |
| `PATCH` | `/consultas/{id}/realizar` | VET designado | `→ REALIZADA` (RN-008) |
| `PATCH` | `/consultas/{id}/nao-compareceu` | VET designado | `→ NAO_COMPARECEU` (RN-038) |

### Colmeia — `/animais/{animalId}` (RN-064 a RN-068)

| Método | Endpoint | Quem | Descrição |
|--------|----------|------|-----------|
| `GET` | `/animais/{animalId}/historico` | TUTOR dono | Histórico sem os registros ocultos, exceto alertas de segurança (RN-068) |
| `GET` | `/animais/{animalId}/historico?contexto=` | VET com vínculo | Completo com consentimento de rede; só o que ele produziu sem (RN-066). Gera log |
| `GET` | `/animais/{animalId}/logs-acesso` | TUTOR dono | Trilha de acessos, 20/pág, mais recente primeiro (RN-067) |

### Prontuário — `/animais/{animalId}/prontuario` (RN-063/088/089)

| Método | Endpoint | Quem | Descrição |
|--------|----------|------|-----------|
| `POST` | `/animais/{animalId}/prontuario` | VET com vínculo | Cria a versão original (uma por animal; a segunda dá `409`) |
| `GET` | `/animais/{animalId}/prontuario` | TUTOR dono / VET com vínculo | Original + todas as correções, em ordem |
| `POST` | `/animais/{animalId}/prontuario/correcoes` | VET com vínculo | Nova versão vinculada à original; justificativa obrigatória após 24h |

### Exames (RN-103/104)

| Método | Endpoint | Quem | Descrição |
|--------|----------|------|-----------|
| `POST` | `/consultas/{consultaId}/solicitacoes-exame` | VET da consulta | Cria a solicitação com N itens |
| `GET` | `/consultas/{consultaId}/solicitacoes-exame` | VET da consulta / TUTOR dono | Leitura; para o tutor, resultado e anexos ficam ocultos até a liberação |
| `PATCH` | `/solicitacoes-exame/itens/{itemId}/resultado` | VET da consulta | Registra o laudo → `ANALISADO` |
| `POST` | `/solicitacoes-exame/itens/{itemId}/anexos` | VET da consulta | Anexa a **URL** do arquivo (a API não recebe binário) |
| `PATCH` | `/solicitacoes-exame/itens/{itemId}/liberar` | VET da consulta | Libera ao Responsável → `RESULTADO_ENVIADO` |
| `PATCH` | `/solicitacoes-exame/itens/{itemId}/cancelar` | VET da consulta | Cancela item ainda não liberado |

### Convenção de erros

| Status | Quando |
|--------|--------|
| `400` | Bean Validation falhou — corpo `{"campo": "mensagem"}` |
| `401` | Sem token ou token inválido — `{"error": "Recurso protegido"}` |
| `403` | Papel errado ou recurso de outro dono |
| `404` | Entidade inexistente ou rota desconhecida |
| `409` | Transição de estado inválida ou violação de unicidade |
| `500` | Erro não tratado |

---

## Segurança e autenticação

JWT stateless com Spring Security:

- `TokenService` emite o token (`com.auth0:java-jwt`); `SecurityFilter` valida a cada requisição
- Senhas em **BCrypt**
- Sessões `STATELESS`, CSRF desabilitado
- `401` e `403` respondem JSON pelo próprio `SecurityConfigurations`

| Recurso | Método | Acesso |
|---------|--------|--------|
| `/auth/**` | POST | Público |
| `/especies`, `/especialidades`, `/veterinarios/**` | GET | Público |
| `/swagger-ui/**`, `/v3/api-docs/**` | GET | Público |
| `/usuarios`, `/especies`, `/especialidades` | POST, PUT, DELETE | `ADMIN` |
| Demais rotas | Qualquer | Autenticado |

O papel só resolve a **primeira** camada. O escopo por dono é verificado no service:
tutor só alcança os próprios animais e consultas; veterinário só as consultas em que é o
designado; e a Colmeia exige vínculo clínico antes de qualquer leitura de histórico.

---

## Banco de dados e migrations

O schema é versionado com **Flyway** em `src/main/resources/db/migration/`.

| Migration | O que faz | RN |
|-----------|-----------|----|
| `V1__baseline_schema` | Baseline espelhando o `DDL.txt` anterior ao Flyway | — |
| `V2__animal_campos_clinicos` | `URL_FOTO_ANIMAL`, `FL_CASTRADO`, `DS_CONDICOES_PREEXIST`, `DS_ALERGIAS`, `DS_MEDICACOES_EM_USO` | RN-081 |
| `V3__evolucao_clinica_flags_rn068` | `FL_OCULTO_RESPONSAVEL`, `FL_ALERTA_SEGURANCA` | RN-068 |
| `V4__consulta_status_nao_compareceu` | Inclui `NAO_COMPARECEU` no CHECK de status | RN-038 |
| `V5__solicitacao_exame_item_liberacao_rn104` | `FL_LIBERADO_RESPONSAVEL`, `DT_LIBERACAO_RESPONSAVEL` | RN-104 |
| `V6__tutor_consentimento_lgpd_rede` | `FL_LGPD_ACEITO`, `DT_LGPD_ACEITO`, `FL_CONSENTIMENTO_REDE`, `DT_CONSENTIMENTO_REDE` | RN-060/062 |
| `V7__log_acesso_prontuario_rn067` | Tabela `TB_LOG_ACESSO_PRONTUARIO` | RN-067 |
| `V8__prontuario_versionamento_rn088_089` | `DS_CONTEUDO_CLINICO`, auto-FK `TB_PRONTUARIO_ID_ORIGINAL`, `DT_HR_CORRECAO`, `CRMV_SOLICITANTE_CORRECAO`, `DS_JUSTIFICATIVA_CORRECAO` | RN-088/089 |

Regras de manutenção:

- `spring.jpa.hibernate.ddl-auto=validate` — o Hibernate **nunca** gera DDL; Flyway é a
  única fonte de verdade do schema.
- `baseline-on-migrate=true` porque o schema da FIAP já existia criado à mão: a migration
  `V1` não é reexecutada nesse ambiente, mas roda normalmente em um schema vazio.
- Ao criar uma migration nova, replique o mesmo DDL no fim de `vetly-database/DDL.txt`.

---

## Como executar

### Pré-requisitos

- Java 21+
- Instância Oracle acessível (o wrapper `./gradlew` dispensa instalar o Gradle)

```bash
git clone https://github.com/challenge-vetly/vetly-java.git
cd vetly-java

./gradlew build        # compila e roda os testes
./gradlew bootRun      # sobe em http://localhost:8080
./gradlew test         # só os testes
```

Na subida, o Flyway aplica as migrations pendentes e o Hibernate valida o mapeamento
contra o schema — divergência entre entidade e tabela derruba a aplicação no start,
de propósito.

---

## Configuração

`src/main/resources/application.properties`:

```properties
spring.application.name=vetly-java

# Segredo de assinatura do JWT — troque em produção
api.security.token.secret=sua-chave-secreta-forte

# Oracle
spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/orcl
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Flyway é dono do schema; Hibernate apenas valida
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1
spring.jpa.hibernate.ddl-auto=validate

# 404 em rota inexistente em vez de página de erro estática
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

# Swagger — duas specs no seletor do Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.urls[0].name=Vetly API - documentada (regras de negocio + mocks)
springdoc.swagger-ui.urls[0].url=/swagger.yaml
springdoc.swagger-ui.urls[1].name=Vetly API - gerada a partir do codigo
springdoc.swagger-ui.urls[1].url=/v3/api-docs
```

> ⚠️ O arquivo versionado hoje contém credenciais reais do ambiente da FIAP. Antes de
> qualquer uso fora da disciplina, mova-as para variáveis de ambiente
> (`SPRING_DATASOURCE_PASSWORD`, `API_SECURITY_TOKEN_SECRET`) e não comite segredo.

---

## Documentação Swagger e roteiro de teste

Suba a aplicação e abra **`http://localhost:8080/swagger-ui.html`**. O seletor no topo da
página oferece duas especificações:

| Spec no seletor | Origem | O que traz |
|-----------------|--------|-----------|
| **Vetly API — documentada (regras de negócio + mocks)** | `swagger.yaml` da raiz, servido em `/swagger.yaml` | Escrita à mão: a RN de cada operação, todas as respostas de erro e **mocks prontos** de request/response para usar no *Try it out* |
| **Vetly API — gerada a partir do código** | springdoc, em `/v3/api-docs` | Reflexo automático dos controllers, sempre em dia com o código |

O `swagger.yaml` tem **uma única cópia**, na raiz do repositório; o `processResources` do
`build.gradle` a copia para os resources e o `OpenApiDocController` a serve. Editar o
arquivo da raiz é suficiente — não há segunda cópia para sincronizar. Ele também pode ser
colado direto em [editor.swagger.io](https://editor.swagger.io).

O `swagger.yaml` traz, em cada operação, exemplos nomeados que formam **um cenário único e
encadeável** (tutor Maria, veterinária Ana, cão Thor) — inclusive os casos de contraste que
provam a regra: histórico com e sem consentimento de rede, exame antes e depois da
liberação ao tutor, correção de prontuário dentro e fora da janela de 24h.

### Roteiro completo com cURL

Os payloads abaixo são os mesmos exemplos do `swagger.yaml`.

```bash
BASE=http://localhost:8080

# 1. Registrar os atores ------------------------------------------------------
curl -sX POST $BASE/auth/register/admin -H 'Content-Type: application/json' -d '{
  "email":"admin@vetly.com.br","senha":"Admin@123"}'

curl -sX POST $BASE/auth/register/tutor -H 'Content-Type: application/json' -d '{
  "email":"maria.souza@vetly.com.br","senha":"Tutor@123","nome":"Maria Souza",
  "cpf":"52998224725","telefone":"(11) 98765-4321"}'

# 2. Admin cria os cadastros de apoio ----------------------------------------
ADMIN=$(curl -sX POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@vetly.com.br","senha":"Admin@123"}' | jq -r .token)

curl -sX POST $BASE/especies -H "Authorization: Bearer $ADMIN" \
  -H 'Content-Type: application/json' -d '{"nome":"CAO"}'
curl -sX POST $BASE/especialidades -H "Authorization: Bearer $ADMIN" \
  -H 'Content-Type: application/json' -d '{"nome":"CARDIOLOGIA",
  "descricao":"Diagnóstico e tratamento de doenças do coração em animais."}'

# 3. Registrar a veterinária (depende dos cadastros acima) --------------------
curl -sX POST $BASE/auth/register/veterinario -H 'Content-Type: application/json' -d '{
  "email":"dra.ana@vetly.com.br","senha":"Senha@456","crmv":"SP-98765",
  "nome":"Dra. Ana Oliveira","cpf":"98765432100","telefone":"(11) 97654-3210",
  "especialidades":["CARDIOLOGIA"],"especies":["CAO"]}'

# 4. Tutor: consentimento, animal e agendamento ------------------------------
TUTOR=$(curl -sX POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"maria.souza@vetly.com.br","senha":"Tutor@123"}' | jq -r .token)

# RN-060/064 — sem isso, o vet só verá o que ele mesmo produzir
curl -sX PUT $BASE/tutores/me/consentimento -H "Authorization: Bearer $TUTOR" \
  -H 'Content-Type: application/json' -d '{"lgpdAceito":true,"consentimentoRede":true}'

ESPECIE=$(curl -s $BASE/especies | jq -r '.[0].id')
ANIMAL=$(curl -sX POST $BASE/animais -H "Authorization: Bearer $TUTOR" \
  -H 'Content-Type: application/json' -d "{
  \"nome\":\"Thor\",\"raca\":\"Golden Retriever\",\"sexo\":\"M\",
  \"dataNascimento\":\"2021-03-14\",\"peso\":32.40,\"especieId\":\"$ESPECIE\",
  \"castrado\":true,\"alergias\":\"Alergia a dipirona.\"}" | jq -r .id)

VET=$(curl -s "$BASE/veterinarios?especialidade=CARDIOLOGIA" | jq -r \
  '.content[0].link.href | split("/") | last')

CONSULTA=$(curl -sX POST $BASE/consultas -H "Authorization: Bearer $TUTOR" \
  -H 'Content-Type: application/json' -d "{
  \"animalId\":\"$ANIMAL\",\"veterinarioId\":\"$VET\",
  \"dataHora\":\"2026-10-14T14:30:00\",\"valor\":180.00,
  \"observacao\":\"Thor está mancando da pata traseira esquerda há três dias.\"}" | jq -r .id)

# 5. Veterinária: Colmeia, prontuário e exame --------------------------------
ANA=$(curl -sX POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"dra.ana@vetly.com.br","senha":"Senha@456"}' | jq -r .token)

# RN-064 — acesso concedido pelo agendamento; RN-067 — gera log
curl -s "$BASE/animais/$ANIMAL/historico?contexto=Consulta%20de%20retorno" \
  -H "Authorization: Bearer $ANA"

# RN-063 — prontuário do animal
curl -sX POST $BASE/animais/$ANIMAL/prontuario -H "Authorization: Bearer $ANA" \
  -H 'Content-Type: application/json' \
  -d '{"conteudoClinico":"Anamnese: claudicação em MPE há 3 dias. Conduta: anti-inflamatório por 5 dias."}'

# RN-088 — correção não sobrescreve; cria nova versão
curl -sX POST $BASE/animais/$ANIMAL/prontuario/correcoes -H "Authorization: Bearer $ANA" \
  -H 'Content-Type: application/json' \
  -d '{"conteudoClinico":"Conduta corrigida: anti-inflamatório por 7 dias.",
       "justificativa":"Ajuste de posologia após revisão do laudo."}'

# RN-103 — solicitação de exame
ITEM=$(curl -sX POST $BASE/consultas/$CONSULTA/solicitacoes-exame \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"observacao":"Jejum de 8 horas antes da coleta.",
       "itens":[{"nomeExame":"Hemograma completo"}]}' | jq -r '.itens[0].id')

curl -sX PATCH $BASE/solicitacoes-exame/itens/$ITEM/resultado \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"descricaoResultado":"Leucograma dentro da normalidade."}'

curl -sX POST $BASE/solicitacoes-exame/itens/$ITEM/anexos \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"urlArquivo":"https://cdn.vetly.com.br/exames/thor-hemograma.pdf",
       "mimeType":"application/pdf"}'

# 6. Provar a RN-104: o tutor NÃO vê o resultado antes da liberação ----------
curl -s $BASE/consultas/$CONSULTA/solicitacoes-exame -H "Authorization: Bearer $TUTOR"
#   → descricaoResultado: null, anexos: []

curl -sX PATCH $BASE/solicitacoes-exame/itens/$ITEM/liberar -H "Authorization: Bearer $ANA"

curl -s $BASE/consultas/$CONSULTA/solicitacoes-exame -H "Authorization: Bearer $TUTOR"
#   → status RESULTADO_ENVIADO, resultado e anexos visíveis

# 7. Encerrar a consulta e auditar os acessos --------------------------------
curl -sX PATCH $BASE/consultas/$CONSULTA/realizar -H "Authorization: Bearer $ANA"   # RN-008
curl -s $BASE/animais/$ANIMAL/logs-acesso -H "Authorization: Bearer $TUTOR"          # RN-067
```

### O que dá para verificar rapidamente

| Teste | Resultado esperado |
|-------|--------------------|
| `POST /animais` sem `peso` | `400` com `{"peso": "nao pode ser vazio"}` — RN-081 |
| Vet **sem** consulta com o animal chamando `/historico` | `403` "veterinário não possui nenhuma consulta com este animal" — RN-064 |
| Tutor com `consentimentoRede: false` e vet lendo o histórico | Só as evoluções do próprio vet; log com `ATENDIMENTO_DIRETO` — RN-066 |
| `PATCH /consultas/{id}/realizar` em consulta já cancelada | `409` "Transição inválida" — RN-038 |
| Segundo `POST .../prontuario` no mesmo animal | `409` "já possui um prontuário" — RN-063 |
| Correção após 24h sem `justificativa` | `409` citando a RN-089 |
| Tutor lendo exame antes de `/liberar` | `descricaoResultado: null`, `anexos: []` — RN-104 |

---

## Roadmap

### Pendências conhecidas

- [ ] **RN-065** — expiração do acesso da Colmeia ao fim do ciclo (consulta + 24h + retornos);
      hoje o vínculo clínico não expira
- [ ] `TB_CONSULTA` — avaliar tornar `TB_VETERINARIO_ID_VETERINARIO` e `TB_ANIMAL_ID_ANIMAL`
      `NOT NULL` (checar dado existente antes)
- [ ] Credenciais fora do `application.properties`
- [ ] Testes automatizados dos serviços de domínio (hoje só existe o smoke test de contexto)

### Backlog de escopo (decidir antes de implementar)

- [ ] `Empresa`/`Administrador` entram no domínio do Java, ou ele fica só para vet autônomo?
- [ ] Validação de CRMV (RN-107) — só relevante se o Java expuser diretório/matching de vets
- [ ] Documentos além do prontuário (atestado, receita, NF) — depende de RN-087 (assinatura)
      e de uma decisão sobre split financeiro
- [ ] LGPD granular por finalidade (RN-061) em vez do aceite simplificado atual
- [ ] `Disponibilidade` — grade horária do veterinário e resolução de conflitos de agenda
