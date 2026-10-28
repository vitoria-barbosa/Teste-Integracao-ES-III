import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.EstoqueInsuficienteException;
import model.Produto;
import repository.ProdutoRepository;
import service.EstoqueService;

public class EstoqueServiceIntegrationTest {
    private static final long PRODUTO_ID = 1;
    private static final int QUANTIDADE_INICIAL = 10;

    private Connection conexao;
    private EstoqueService estoqueService;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        conexao = DriverManager.getConnection("jdbc:h2:mem:estoque;DB_CLOSE_DELAY=-1");

        try (Statement statement = conexao.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS produtos");
            statement.execute(Files.readString(Path.of("script.sql")));
        }

        ProdutoRepository repositorio = new ProdutoRepository(conexao);
        repositorio.salvar(new Produto(PRODUTO_ID, "Produto inicial", QUANTIDADE_INICIAL));
        estoqueService = new EstoqueService(repositorio);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
    }

    @Test
    void testAdicionarEstoqueAtualizaBanco() throws SQLException {
        estoqueService.adicionarEstoque(PRODUTO_ID, 5);

        assertEquals(QUANTIDADE_INICIAL + 5, consultarQuantidadeNoBanco());
    }

    @Test
    void testVenderProdutoComEstoqueSuficiente() throws SQLException {
        estoqueService.venderProduto(PRODUTO_ID, 4);

        assertEquals(QUANTIDADE_INICIAL - 4, consultarQuantidadeNoBanco());
    }

    @Test
    void testDuasVendasSeguidasAtualizamSaldoNoBanco() throws SQLException {
        estoqueService.venderProduto(PRODUTO_ID, 3);
        estoqueService.venderProduto(PRODUTO_ID, 2);

        assertEquals(QUANTIDADE_INICIAL - 3 - 2, consultarQuantidadeNoBanco());
    }

    @Test
    void testVenderProdutoComEstoqueInsuficienteNaoAlteraBanco() throws SQLException {
        assertThrows(EstoqueInsuficienteException.class,
                () -> estoqueService.venderProduto(PRODUTO_ID, QUANTIDADE_INICIAL + 1));

        assertEquals(QUANTIDADE_INICIAL, consultarQuantidadeNoBanco());
    }

    private int consultarQuantidadeNoBanco() throws SQLException {
        String sql = "SELECT quantidade FROM produtos WHERE id = ?";
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, PRODUTO_ID);
            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    throw new SQLException("Produto de teste não encontrado no banco");
                }
                return resultado.getInt("quantidade");
            }
        }
    }

}
