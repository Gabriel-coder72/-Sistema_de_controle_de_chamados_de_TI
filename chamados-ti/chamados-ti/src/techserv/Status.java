package techserv;

public enum Status {
    ABERTO("aberto"), EM_ATENDIMENTO("em atendimento"), CONCLUIDO("concluído");

    private final String rotulo;

    Status(String rotulo) { this.rotulo = rotulo; }

    public String rotulo() { return rotulo; }

    public Status proximo() {
        return switch (this) {
            case ABERTO -> EM_ATENDIMENTO;
            case EM_ATENDIMENTO -> CONCLUIDO;
            case CONCLUIDO -> null;
        };
    }

    public static Status de(String texto) {
        String t = Prioridade.normalizar(texto);
        for (Status s : values()) {
            if (Prioridade.normalizar(s.rotulo).equals(t)) return s;
        }
        return null;
    }
}
