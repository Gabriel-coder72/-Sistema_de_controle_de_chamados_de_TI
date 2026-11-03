package techserv;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "chamados.db";
        ChamadoService service;
        try {
            service = new ChamadoService(new ChamadoRepositorySqlite(arquivo));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println("Verifique se o driver sqlite-jdbc está na pasta lib/ (veja o LEIA-ME).");
            return;
        }

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            try {
                String opcao = in.nextLine().trim();
                switch (opcao) {
                    case "1" -> cadastrar(service);
                    case "2" -> imprimir(service.listar());
                    case "3" -> consultar(service);
                    case "4" -> atualizarStatus(service);
                    case "5" -> filtrar(service);
                    case "6" -> resumo(service.resumo());
                    case "7" -> { service.carregarDemonstracao(); System.out.println("Dados de demonstração cadastrados."); }
                    case "0" -> rodando = false;
                    default -> System.out.println("Opção inválida. Digite um número do menu.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("⚠ " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("✖ " + e.getMessage());
            } catch (NoSuchElementException e) {
                rodando = false;
            }
        }
        System.out.println("Até logo!");
    }

    private static void exibirMenu() {
        System.out.println("""

                ===== TechServ - Controle de Chamados de TI =====
                1 - Cadastrar chamado
                2 - Listar chamados
                3 - Consultar chamado por ID
                4 - Atualizar status
                5 - Filtrar chamados
                6 - Resumo por status
                7 - Carregar dados de demonstração
                0 - Sair""");
        System.out.print("Escolha: ");
    }

    private static String ler(String rotulo) {
        System.out.print(rotulo);
        return in.nextLine();
    }

    private static long lerId() {
        String texto = ler("ID do chamado: ").trim();
        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("ID inválido: informe apenas números.");
        }
    }

    private static void cadastrar(ChamadoService service) {
        String sol = ler("Solicitante: ");
        String desc = ler("Descrição (mín. 10 caracteres): ");
        String prio = ler("Prioridade (baixa/média/alta): ");
        Chamado c = service.cadastrar(sol, desc, prio);
        System.out.println("Chamado #" + c.id() + " criado com status " + c.status().rotulo() + ".");
    }

    private static void consultar(ChamadoService service) {
        Chamado c = service.consultar(lerId());
        System.out.println("""
                ID.........: %d
                Solicitante: %s
                Descrição..: %s
                Prioridade.: %s
                Status.....: %s
                Abertura...: %s"""
                .formatted(c.id(), c.solicitante(), c.descricao(), c.prioridade().rotulo(),
                        c.status().rotulo(), c.dataAbertura().format(DATA)));
    }

    private static void atualizarStatus(ChamadoService service) {
        long id = lerId();
        String novo = ler("Novo status (em atendimento/concluído): ");
        Chamado c = service.atualizarStatus(id, novo);
        System.out.println("Chamado #" + c.id() + " agora está " + c.status().rotulo() + ".");
    }

    private static void filtrar(ChamadoService service) {
        String tipo = ler("Filtrar por (1) prioridade ou (2) status? ").trim();
        switch (tipo) {
            case "1" -> imprimir(service.filtrarPorPrioridade(ler("Prioridade: ")));
            case "2" -> imprimir(service.filtrarPorStatus(ler("Status: ")));
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void imprimir(List<Chamado> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum chamado encontrado.");
            return;
        }
        System.out.printf("%-4s %-14s %-10s %-15s %-17s %s%n",
                "ID", "Solicitante", "Prioridade", "Status", "Abertura", "Descrição");
        for (Chamado c : lista) {
            System.out.printf("%-4d %-14s %-10s %-15s %-17s %s%n", c.id(), c.solicitante(),
                    c.prioridade().rotulo(), c.status().rotulo(), c.dataAbertura().format(DATA), c.descricao());
        }
    }

    private static void resumo(Map<Status, Integer> mapa) {
        System.out.println("Resumo por status:");
        for (Status s : Status.values()) System.out.println("  " + s.rotulo() + ": " + mapa.get(s));
    }
}
