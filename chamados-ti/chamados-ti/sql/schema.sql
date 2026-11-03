CREATE TABLE IF NOT EXISTS chamado (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    solicitante   TEXT NOT NULL CHECK (length(trim(solicitante)) >= 3),
    descricao     TEXT NOT NULL CHECK (length(trim(descricao)) >= 10),
    prioridade    TEXT NOT NULL CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA')),
    status        TEXT NOT NULL DEFAULT 'ABERTO'
                  CHECK (status IN ('ABERTO', 'EM_ATENDIMENTO', 'CONCLUIDO')),
    data_abertura TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE INDEX IF NOT EXISTS idx_chamado_status     ON chamado (status);
CREATE INDEX IF NOT EXISTS idx_chamado_prioridade ON chamado (prioridade);
