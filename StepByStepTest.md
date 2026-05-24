# Arquivo de teste
Siga as instrucoes desse arquivo para testar os recursos implementados, seguindo a logica do fluxo do usuario

---

**Fique a vontade para testar a validacao em todos os endpoints, as mensagens de retorno estao bem descritivas**

---
## Endpoints publicos
[GET] http://localhost:8080/especies //nao tem paginacao pq eh um enum pequeno

[GET] http://localhost:8080/especialidades

## Cadastro admin
[POST] http://localhost:8080/auth/register/admin

```JSON
{
"senha": "Senha123!",
"email": "luizadmin@email.com"
}
```

## Login como admin
[POST] http://localhost:8080/auth/login

```JSON
{
"senha": "Senha123!",
"email": "luizadmin@email.com"
}
```

```copie o token gerado```

## Criando Especialidade
[POST] http://localhost:8080/especialidades

`Auth: Bearer Token`
```JSON
{
"nome": "ACUPUNTURA",
"descricao": "Descricao qualquer acupuntura"
}
```

## Criando Especie
[POST] http://localhost:8080/especies

`Auth: Bearer Token`
```JSON
{
  "nome": "AVE",
  "descricao": "Descricao qualquer ave"
}
```

## Cadastro veterinario
[POST] http://localhost:8080/auth/register/veterinario
```JSON
{
"senha": "Senha123!",
"telefone": "(11) 98665-4320",
"especialidades": ["ACUPUNTURA"],
"crmv": "4321",
"especies": ["AVE"],
"cpf": "25027778015",
"nome": "luiz",
"email":"luizvet@email.com"
}
```

## Busca veterinarios por especialidade
[GET] http://localhost:8080/veterinarios?especialidade=acupuntura

