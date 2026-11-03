# TechServ – Controle de Chamados de TI (Java + SQLite)

Aplicação de terminal para registrar, consultar, filtrar e atualizar chamados de suporte.

## Requisitos
- **JDK 17 ou superior** (testado com JDK 21) – precisa do `javac`.
- **Driver sqlite-jdbc** em `lib/`. Recomendado: **sqlite-jdbc 3.36.0.3** (não exige outras dependências).
  Baixe em https://github.com/xerial/sqlite-jdbc/releases ou no Maven Central
  (`org.xerial:sqlite-jdbc:3.36.0.3`) e salve como `lib/sqlite-jdbc.jar`.

## Como compilar e executar

**Linux / macOS**
```bash
mkdir -p out
javac -encoding UTF-8 -d out src/techserv/*.java
java -Dstdout.encoding=UTF-8 -cp "out:lib/sqlite-jdbc.jar" techserv.Main
```

**Windows (PowerShell/CMD)**
```bat
mkdir out
javac -encoding UTF-8 -d out src\techserv\*.java
java -Dstdout.encoding=UTF-8 -cp "out;lib\sqlite-jdbc.jar" techserv.Main
```

O banco `chamados.db` e a tabela são criados automaticamente na primeira execução
(o script equivalente está em `sql/schema.sql`). Os dados persistem entre execuções.
Para usar outro arquivo de banco: `... techserv.Main caminho/outro.db`.

## Como executar os testes automatizados
```bash
javac -encoding UTF-8 -d out src/techserv/*.java test/techserv/*.java
java -Dstdout.encoding=UTF-8 -cp "out:lib/sqlite-jdbc.jar" techserv.TestesAutomatizados
```
(no Windows troque `:` por `;`). Sem o driver, o T09 é pulado; com o driver ele roda contra um SQLite real.

## Demonstração (seção 6 do enunciado)
No menu, a opção **7** cadastra os 4 chamados e aplica as transições pedidas. Depois use a opção
**5** (prioridade *alta*) e a opção **6** (resumo: 2 abertos, 1 em atendimento, 1 concluído).
Use a opção 7 só uma vez, ou apague `chamados.db` para recomeçar.

## Estrutura
```
src/techserv/   Main (menu), ChamadoService (regras), ChamadoRepository(+Sqlite) (JDBC),
                Chamado, Status, Prioridade, RegraNegocioException
test/techserv/  TestesAutomatizados (T01–T10), ChamadoRepositoryMemoria
sql/schema.sql  Criação da tabela
docs/           planejamento.md (requisitos, menu, modelo de dados) e relatorio-testes.md
lib/            sqlite-jdbc.jar (você adiciona)
```
