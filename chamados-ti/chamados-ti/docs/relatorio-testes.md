# Relatório de testes

Execução: `TestesAutomatizados` (JDK 21). T01–T08 e T10 rodaram contra o repositório em memória
(mesmas regras de `ChamadoService`). As restrições `CHECK` e as consultas do SQL foram conferidas à parte
com o SQLite (script `sql/schema.sql`). **T09 exige o driver sqlite-jdbc e deve ser executado por você** (veja abaixo).

| Teste | Resultado obtido | Aprovado / Reprovado | Evidência ou correção |
|---|---|---|---|
| T01 | Chamado criado com id=1, status aberto e data preenchida | Aprovado | Saída do `TestesAutomatizados` |
| T02 | "Solicitante obrigatório, com pelo menos 3 caracteres…" | Aprovado | Banco também recusa (CHECK) |
| T03 | "Descrição obrigatória, com pelo menos 10 caracteres…" | Aprovado | Banco também recusa (CHECK) |
| T04 | "Prioridade inválida. Use: baixa, média ou alta." | Aprovado | Banco também recusa (CHECK) |
| T05 | "Chamado #999 não encontrado." | Aprovado | Programa segue rodando |
| T06 | Status atualizado para em atendimento | Aprovado | — |
| T07 | "Transição não permitida: aberto → concluído…"; status permaneceu aberto | Aprovado | — |
| T08 | Status concluído; chamado continua consultável | Aprovado | — |
| T09 | **Não executado neste ambiente** (sem driver SQLite) | Pendente | Rodar `TestesAutomatizados` com o driver em `lib/`, ou fechar/abrir o programa e usar a opção 2 |
| T10 | altas=2, abertos=2, resumo {aberto=2, em atendimento=1, concluído=1} | Aprovado | Mesma contagem no SQLite: `GROUP BY status` |

**Falhas encontradas/correções:** nenhuma falha de regra. Observação: no console, acentos podem aparecer como `?`
se o Java não usar UTF-8; solução: executar com `-Dstdout.encoding=UTF-8`.
