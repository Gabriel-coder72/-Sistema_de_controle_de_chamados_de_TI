package techserv;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ChamadoRepositoryMemoria implements ChamadoRepository {
    private final Map<Long, Chamado> dados = new TreeMap<>();
    private long proximoId = 1;

    public long inserir(String s, String d, Prioridade p) {
        long id = proximoId++;
        dados.put(id, new Chamado(id, s, d, p, Status.ABERTO,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        return id;
    }

    public Optional<Chamado> buscarPorId(long id) { return Optional.ofNullable(dados.get(id)); }

    public List<Chamado> listar() { return new ArrayList<>(dados.values()); }

    public List<Chamado> filtrarPorPrioridade(Prioridade p) {
        return dados.values().stream().filter(c -> c.prioridade() == p).toList();
    }

    public List<Chamado> filtrarPorStatus(Status s) {
        return dados.values().stream().filter(c -> c.status() == s).toList();
    }

    public void atualizarStatus(long id, Status novo) {
        Chamado c = dados.get(id);
        dados.put(id, new Chamado(c.id(), c.solicitante(), c.descricao(), c.prioridade(), novo, c.dataAbertura()));
    }

    public Map<Status, Integer> contarPorStatus() {
        Map<Status, Integer> m = new EnumMap<>(Status.class);
        for (Status s : Status.values()) m.put(s, 0);
        dados.values().forEach(c -> m.merge(c.status(), 1, Integer::sum));
        return m;
    }
}
