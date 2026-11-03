package techserv;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TestesAutomatizados {

    private static int aprovados = 0, reprovados = 0, pulados = 0;

    public static void main(String[] args) throws Exception {
        t01(); t02(); t03(); t04(); t05(); t06(); t07(); t08(); t09(); t10();
        System.out.printf("%nAprovados: %d | Reprovados: %d | Pulados: %d%n", aprovados, reprovados, pulados);
        System.exit(reprovados == 0 ? 0 : 1);
    }

    static ChamadoService novo() { return new ChamadoService(new ChamadoRepositoryMemoria()); }

    static void resultado(String id, boolean ok, String detalhe) {
        if (ok) aprovados++; else reprovados++;
        System.out.printf("%s | %s | %s%n", id, ok ? "APROVADO " : "REPROVADO", detalhe);
    }

    static String erroDe(Runnable acao) {
        try { acao.run(); return null; }
        catch (RegraNegocioException e) { return e.getMessage(); }
    }

    static void t01() {
        Chamado c = novo().cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
        resultado("T01", c.id() > 0 && c.dataAbertura() != null && c.status() == Status.ABERTO,
                "id=" + c.id() + ", status=" + c.status().rotulo() + ", data=" + c.dataAbertura());
    }

    static void t02() {
        String msg = erroDe(() -> novo().cadastrar("     ", "Descrição válida aqui", "alta"));
        resultado("T02", msg != null, msg);
    }

    static void t03() {
        String msg = erroDe(() -> novo().cadastrar("Ana Souza", "curta", "alta"));
        resultado("T03", msg != null, msg);
    }

    static void t04() {
        String msg = erroDe(() -> novo().cadastrar("Ana Souza", "Descrição válida aqui", "urgente"));
        resultado("T04", msg != null, msg);
    }

    static void t05() {
        String msg = erroDe(() -> novo().consultar(999));
        resultado("T05", msg != null && msg.contains("não encontrado"), msg);
    }

    static void t06() {
        ChamadoService s = novo();
        Chamado c = s.cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
        Chamado r = s.atualizarStatus(c.id(), "em atendimento");
        resultado("T06", r.status() == Status.EM_ATENDIMENTO, "status=" + r.status().rotulo());
    }

    static void t07() {
        ChamadoService s = novo();
        Chamado c = s.cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
        String msg = erroDe(() -> s.atualizarStatus(c.id(), "concluído"));
        boolean manteve = s.consultar(c.id()).status() == Status.ABERTO;
        resultado("T07", msg != null && manteve, msg + " | status mantido=" + manteve);
    }

    static void t08() {
        ChamadoService s = novo();
        Chamado c = s.cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
        s.atualizarStatus(c.id(), "em atendimento");
        Chamado r = s.atualizarStatus(c.id(), "concluído");
        boolean consultavel = s.consultar(c.id()).status() == Status.CONCLUIDO;
        resultado("T08", r.status() == Status.CONCLUIDO && consultavel,
                "status=" + r.status().rotulo() + ", ainda consultável=" + consultavel);
    }

    static void t09() throws Exception {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            pulados++;
            System.out.println("T09 | PULADO    | driver sqlite-jdbc não encontrado no classpath (coloque em lib/)");
            return;
        }
        File arq = File.createTempFile("chamados-teste", ".db");
        arq.delete();
        try {
            ChamadoService s1 = new ChamadoService(new ChamadoRepositorySqlite(arq.getPath()));
            Chamado c = s1.cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "alta");
            s1.atualizarStatus(c.id(), "em atendimento");
            ChamadoService s2 = new ChamadoService(new ChamadoRepositorySqlite(arq.getPath()));
            Chamado lido = s2.consultar(c.id());
            resultado("T09", lido.status() == Status.EM_ATENDIMENTO && lido.solicitante().equals("Ana Souza"),
                    "lido após reabrir: #" + lido.id() + " " + lido.solicitante() + " / " + lido.status().rotulo());
        } finally {
            arq.delete();
        }
    }

    static void t10() {
        ChamadoService s = novo();
        s.carregarDemonstracao();
        List<Chamado> altas = s.filtrarPorPrioridade("alta");
        List<Chamado> abertos = s.filtrarPorStatus("aberto");
        Map<Status, Integer> r = s.resumo();
        boolean ok = altas.size() == 2 && abertos.size() == 2
                && r.get(Status.ABERTO) == 2 && r.get(Status.EM_ATENDIMENTO) == 1 && r.get(Status.CONCLUIDO) == 1;
        resultado("T10", ok, "altas=" + altas.size() + ", abertos=" + abertos.size() + ", resumo=" + r);
    }
}
