package techserv;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ChamadoRepositorySqlite implements ChamadoRepository {

    private static final String SCHEMA = """
            CREATE TABLE IF NOT EXISTS chamado (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                solicitante   TEXT NOT NULL CHECK (length(trim(solicitante)) >= 3),
                descricao     TEXT NOT NULL CHECK (length(trim(descricao)) >= 10),
                prioridade    TEXT NOT NULL CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA')),
                status        TEXT NOT NULL DEFAULT 'ABERTO'
                              CHECK (status IN ('ABERTO', 'EM_ATENDIMENTO', 'CONCLUIDO')),
                data_abertura TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
            )""";

    private static final String COLUNAS =
            "id, solicitante, descricao, prioridade, status, data_abertura";

    private final String url;

    public ChamadoRepositorySqlite(String arquivoBanco) {
        this.url = "jdbc:sqlite:" + arquivoBanco;
        try (Connection c = conectar(); Statement st = c.createStatement()) {
            st.execute(SCHEMA);
            st.execute("CREATE INDEX IF NOT EXISTS idx_chamado_status ON chamado (status)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_chamado_prioridade ON chamado (prioridade)");
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível preparar o banco: " + e.getMessage(), e);
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public long inserir(String solicitante, String descricao, Prioridade prioridade) {
        String sql = "INSERT INTO chamado (solicitante, descricao, prioridade, status, data_abertura) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, solicitante);
            ps.setString(2, descricao);
            ps.setString(3, prioridade.name());
            ps.setString(4, Status.ABERTO.name());
            ps.setString(5, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw erro(e);
        }
    }

    @Override
    public Optional<Chamado> buscarPorId(long id) {
        String sql = "SELECT " + COLUNAS + " FROM chamado WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw erro(e);
        }
    }

    @Override
    public List<Chamado> listar() {
        return consultar("SELECT " + COLUNAS + " FROM chamado ORDER BY id", null);
    }

    @Override
    public List<Chamado> filtrarPorPrioridade(Prioridade prioridade) {
        return consultar("SELECT " + COLUNAS + " FROM chamado WHERE prioridade = ? ORDER BY id",
                prioridade.name());
    }

    @Override
    public List<Chamado> filtrarPorStatus(Status status) {
        return consultar("SELECT " + COLUNAS + " FROM chamado WHERE status = ? ORDER BY id",
                status.name());
    }

    @Override
    public void atualizarStatus(long id, Status novoStatus) {
        String sql = "UPDATE chamado SET status = ? WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, novoStatus.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw erro(e);
        }
    }

    @Override
    public Map<Status, Integer> contarPorStatus() {
        Map<Status, Integer> mapa = new EnumMap<>(Status.class);
        for (Status s : Status.values()) mapa.put(s, 0);
        String sql = "SELECT status, COUNT(*) FROM chamado GROUP BY status";
        try (Connection c = conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) mapa.put(Status.valueOf(rs.getString(1)), rs.getInt(2));
            return mapa;
        } catch (SQLException e) {
            throw erro(e);
        }
    }

    private List<Chamado> consultar(String sql, String parametro) {
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (parametro != null) ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                List<Chamado> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapear(rs));
                return lista;
            }
        } catch (SQLException e) {
            throw erro(e);
        }
    }

    private Chamado mapear(ResultSet rs) throws SQLException {
        return new Chamado(rs.getLong("id"), rs.getString("solicitante"), rs.getString("descricao"),
                Prioridade.valueOf(rs.getString("prioridade")), Status.valueOf(rs.getString("status")),
                LocalDateTime.parse(rs.getString("data_abertura").replace(' ', 'T')));
    }

    private IllegalStateException erro(SQLException e) {
        return new IllegalStateException("Erro de banco de dados: " + e.getMessage(), e);
    }
}
