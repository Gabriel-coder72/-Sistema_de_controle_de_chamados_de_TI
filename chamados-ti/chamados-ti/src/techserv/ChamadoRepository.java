package techserv;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChamadoRepository {

    long inserir(String solicitante, String descricao, Prioridade prioridade);

    Optional<Chamado> buscarPorId(long id);

    List<Chamado> listar();

    List<Chamado> filtrarPorPrioridade(Prioridade prioridade);

    List<Chamado> filtrarPorStatus(Status status);

    void atualizarStatus(long id, Status novoStatus);

    Map<Status, Integer> contarPorStatus();
}
