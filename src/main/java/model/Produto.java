package model;
public class Produto {
    private Long id;
    private String nome;
    private int quantidade;

    public Produto(Long id, String nome, int quantidade) {
    this.id = id;
    this.nome = nome;
    this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
}
