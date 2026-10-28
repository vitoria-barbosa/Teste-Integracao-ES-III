package service;

import java.sql.SQLException;

import exception.EstoqueInsuficienteException;
import model.Produto;
import repository.ProdutoRepository;

public class EstoqueService {
    private final ProdutoRepository repositorio;

    public EstoqueService(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void adicionarEstoque(Long produtoId, int quantidade) throws SQLException {
        Produto produto = repositorio.buscarPorId(produtoId);
        int novaQuantidade = produto.getQuantidade() + quantidade;
        repositorio.atualizarQuantidade(produtoId, novaQuantidade);
    }

    public void venderProduto(Long produtoId, int quantidade) throws SQLException {
        Produto produto = repositorio.buscarPorId(produtoId);
        if (quantidade > produto.getQuantidade()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto " + produtoId);
        }
        repositorio.atualizarQuantidade(produtoId, produto.getQuantidade() - quantidade);
    }
}
