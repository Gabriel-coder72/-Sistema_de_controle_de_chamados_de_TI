package techserv;

import java.text.Normalizer;

public enum Prioridade {
    BAIXA("baixa"), MEDIA("média"), ALTA("alta");

    private final String rotulo;

    Prioridade(String rotulo) { this.rotulo = rotulo; }

    public String rotulo() { return rotulo; }

    public static Prioridade de(String texto) {
        String t = normalizar(texto);
        for (Prioridade p : values()) {
            if (normalizar(p.rotulo).equals(t)) return p;
        }
        return null;
    }

    static String normalizar(String s) {
        if (s == null) return "";
        String semAcento = Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().replaceAll("\\s+", " ");
    }
}
