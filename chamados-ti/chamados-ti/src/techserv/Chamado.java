package techserv;

import java.time.LocalDateTime;

public record Chamado(long id, String solicitante, String descricao,
                      Prioridade prioridade, Status status, LocalDateTime dataAbertura) {
}
