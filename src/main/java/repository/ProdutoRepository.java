package repository;

import java.sql.*;
import model.Produto;

public class ProdutoRepository {
    private final Connection conexao;

    public ProdutoRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvar(Produto produto) throws SQLException {
        String sql = "INSERT INTO produtos (id, nome, quantidade) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, produto.getId());
            ps.setString(2, produto.getNome());
            ps.setInt(3, produto.getQuantidade());
            ps.executeUpdate();
        }
    }

    public Produto buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Produto(rs.getLong("id"), rs.getString("nome"),
                        rs.getInt("quantidade"));
            }
            return null;
        }
    }

    public void atualizarQuantidade(Long id, int novaQuantidade) throws SQLException {
        String sql = "UPDATE produtos SET quantidade = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, novaQuantidade);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
}
