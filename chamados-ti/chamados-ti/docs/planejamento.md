# Planejamento – Controle de Chamados de TI

## Requisitos funcionais
- RF01 Cadastrar chamado (solicitante, descrição, prioridade); status inicial *aberto*, data e ID automáticos.
- RF02 Listar chamados com identificador e status (inclusive concluídos).
- RF03 Consultar chamado por identificador; ID inexistente gera mensagem clara sem encerrar o programa.
- RF04 Atualizar status respeitando aberto → em atendimento → concluído.
- RF05 Filtrar por prioridade ou por status.
- RF06 Apresentar resumo com a quantidade de chamados por status.
- RF07 Persistir os dados em banco SQLite.

## Regras de negócio
Solicitante ≥ 3 caracteres e descrição ≥ 10 (após `trim`); prioridade ∈ {baixa, média, alta};
status ∈ {aberto, em atendimento, concluído}; consultas SQL sempre parametrizadas (`PreparedStatement`).

## Menu
```
1 - Cadastrar chamado        5 - Filtrar chamados
2 - Listar chamados          6 - Resumo por status
3 - Consultar chamado por ID 7 - Carregar dados de demonstração
4 - Atualizar status         0 - Sair
```

## Modelo de dados – tabela `chamado`
| Campo | Tipo | Restrições |
|---|---|---|
| id | INTEGER | **Chave primária**, AUTOINCREMENT |
| solicitante | TEXT | NOT NULL, `length(trim()) >= 3` |
| descricao | TEXT | NOT NULL, `length(trim()) >= 10` |
| prioridade | TEXT | NOT NULL, IN ('BAIXA','MEDIA','ALTA') |
| status | TEXT | NOT NULL, DEFAULT 'ABERTO', IN ('ABERTO','EM_ATENDIMENTO','CONCLUIDO') |
| data_abertura | TEXT (ISO-8601) | NOT NULL, preenchida automaticamente |

As regras são validadas na camada de serviço (mensagens ao atendente) e reforçadas por `CHECK` no banco.
