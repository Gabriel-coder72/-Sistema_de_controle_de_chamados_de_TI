package techserv;

import java.util.List;
import java.util.Map;

public class ChamadoService {

    private final ChamadoRepository repo;

    public ChamadoService(ChamadoRepository repo) { this.repo = repo; }

    public Chamado cadastrar(String solicitante, String descricao, String prioridadeTexto) {
        String sol = solicitante == null ? "" : solicitante.trim();
        String desc = descricao == null ? "" : descricao.trim();

        if (sol.length() < 3)
            throw new RegraNegocioException(
                    "Solicitante obrigatório, com pelo menos 3 caracteres (espaços nas pontas não contam).");
        if (desc.length() < 10)
            throw new RegraNegocioException(
                    "Descrição obrigatória, com pelo menos 10 caracteres (espaços nas pontas não contam).");
        Prioridade prioridade = Prioridade.de(prioridadeTexto);
        if (prioridade == null)
            throw new RegraNegocioException("Prioridade inválida. Use: baixa, média ou alta.");

        long id = repo.inserir(sol, desc, prioridade);
        return consultar(id);
    }

    public Chamado consultar(long id) {
        return repo.buscarPorId(id).orElseThrow(
                () -> new RegraNegocioException("Chamado #" + id + " não encontrado."));
    }

    public List<Chamado> listar() { return repo.listar(); }

    public List<Chamado> filtrarPorPrioridade(String texto) {
        Prioridade p = Prioridade.de(texto);
        if (p == null) throw new RegraNegocioException("Prioridade inválida. Use: baixa, média ou alta.");
        return repo.filtrarPorPrioridade(p);
    }

    public List<Chamado> filtrarPorStatus(String texto) {
        Status s = Status.de(texto);
        if (s == null)
            throw new RegraNegocioException("Status inválido. Use: aberto, em atendimento ou concluído.");
        return repo.filtrarPorStatus(s);
    }

    public Chamado atualizarStatus(long id, String novoStatusTexto) {
        Chamado atual = consultar(id);
        Status novo = Status.de(novoStatusTexto);
        if (novo == null)
            throw new RegraNegocioException("Status inválido. Use: aberto, em atendimento ou concluído.");

        Status permitido = atual.status().proximo();
        if (permitido == null)
            throw new RegraNegocioException("Chamado #" + id + " já está concluído e não pode mais ser alterado.");
        if (novo != permitido)
            throw new RegraNegocioException("Transição não permitida: " + atual.status().rotulo()
                    + " → " + novo.rotulo() + ". O próximo status permitido é: " + permitido.rotulo() + ".");

        repo.atualizarStatus(id, novo);
        return consultar(id);
    }

    public Map<Status, Integer> resumo() { return repo.contarPorStatus(); }

    public void carregarDemonstracao() {
        Chamado ana = cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
        Chamado bruno = cadastrar("Bruno Lima", "Impressora do setor não imprime documentos.", "média");
        cadastrar("Carla Santos", "Solicitação de instalação de editor de texto.", "baixa");
        cadastrar("Diego Alves", "Sistema de estoque apresenta erro no acesso.", "alta");
        atualizarStatus(ana.id(), "em atendimento");
        atualizarStatus(bruno.id(), "em atendimento");
        atualizarStatus(bruno.id(), "concluído");
    }
}
