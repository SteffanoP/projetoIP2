package negocio.beans;

import java.time.LocalDateTime;

public class Movimentacao {
    //Variáveis
    private String descricao;
    private LocalDateTime instante;
    private Cliente cliente;
    private TipoMovimentacao tipoMovimentacao;
    private double valor;

    //Construtores
    public Movimentacao() {
    }

    //Métodos
    @Override
    public String toString() {
        return "Movimentacao{" +
                "descricao='" + descricao + '\'' +
                ", instante=" + instante +
                ", cliente=" + cliente +
                ", tipoMovimentacao=" + tipoMovimentacao +
                ", valor=" + valor +
                '}';
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public void setInstante(LocalDateTime instante) {
        this.instante = instante;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
