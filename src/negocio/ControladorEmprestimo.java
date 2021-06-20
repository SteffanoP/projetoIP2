package negocio;

import dados.Repositorio;
import dados.RepositorioCRUD;
import exceptions.EmprestimoDuplicadoException;
import exceptions.EmprestimoInexistenteException;
import exceptions.ObjetoDuplicadoException;
import negocio.beans.Cliente;
import negocio.beans.Empregado;
import negocio.beans.Emprestimo;
import negocio.beans.Proposta;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ControladorEmprestimo {
    private static final long QTD_DIAS_PARA_1_PAGAMENTO = 30;
    private static final float CONFIANCA_PAGAMENTO_INICIAL = 50.0F;
    private Repositorio<Emprestimo> repoEmprestimo;

    public ControladorEmprestimo() {
        this.repoEmprestimo = new RepositorioCRUD<>();
    }


    public void criarEmprestimo(Proposta proposta) {
    /**
     * Método que cria um empréstimo a partir de uma proposta aprovada por um empregado, neste método são aplicadas
     * regras de negócio, como a primeira data de pagamento do empréstimo que se baseia na
     * {@code QTD_DIAS_PARA_1_PAGAMENTO}, o responsável pelo empréstimo {@code empregado}, e da
     * {@code CONFIANCA_PAGAMENTO_INICIAL}, que se trata da probalidade do próximo pagamento.
     *
     * @param proposta se refere a uma proposta aprovada e a ser transformada em empréstimo aprovado.
     * @param empregado se refere ao objeto {@code Empregado} que será responsável pelo empréstimo.
     * @throws EmprestimoDuplicadoException poderá acontecer caso o {@code Emprestimo} já esteja cadastro no sistema de
     * {@code repoEmprestimo}.
     */
    public void criarEmprestimo(Proposta proposta, Empregado empregado) throws EmprestimoDuplicadoException {
        //Evitar criação de empréstimo fantasma
        if (proposta == null || empregado == null) return;

        Emprestimo emprestimo = new Emprestimo(proposta);
        LocalDate dataPagamento = LocalDate.now();

        //Atribuindo a primeira data de pagamento do empréstimo
        dataPagamento = dataPagamento.plusDays(QTD_DIAS_PARA_1_PAGAMENTO);
        emprestimo.setDataPagamento(dataPagamento);

        //Atribuindo o empregado responsável pelo empréstimo
        emprestimo.setEmpregado(empregado);

        //Atribuindo a confiança de pagamento inicial
        emprestimo.setConfiancaPagamento(CONFIANCA_PAGAMENTO_INICIAL);

        try {
            repoEmprestimo.inserir(emprestimo);
        } catch (ObjetoDuplicadoException e) {
            throw new EmprestimoDuplicadoException("Parece que esse empréstimo já existe!");
        }
    }

    /**
     * Método que procura por detalhes de um empréstimo efetuado em uma determinada {@code dataEmprestimo} por um
     * {@code cliente}, visto que, há apenas 1 empréstimo do mesmo cliente por dia. O método lista os empréstimos de
     * um cliente e procura por empréstimos por data.
     *
     * @param cliente se refere ao {@code Cliente} que realizou o {@code Emprestimo}.
     * @param dataEmprestimo se refere a data em que o {@code Cliente} realizou o {@code Emprestimo}.
     * @return uma {@code String} de dados, com base no método {@code toString()} do objeto {@code Emprestimo}.
     * @throws EmprestimoInexistenteException poderá acontecer caso não exista um empréstimo para esse {@code cliente}
     * ou não existe um empréstimo para esse {@code cliente} na determinada {@code dataEmprestimo}.
     */
    public String emprestimoEmDetalhe(Cliente cliente, LocalDate dataEmprestimo) throws EmprestimoInexistenteException {
        if (cliente == null || dataEmprestimo == null) throw new EmprestimoInexistenteException("Essa requisição " +
                "parece inválida!");

        List<Emprestimo> listaEmprestimosCliente = this.listarEmprestimosCliente(cliente.getUid());
        String emprestimoEmDetalhe = "";

        boolean emprestimoEncontrado = false;
        for (int i = 0; i < listaEmprestimosCliente.size() && !emprestimoEncontrado; i++) {
            Emprestimo emprestimo = listaEmprestimosCliente.get(i);
            if (dataEmprestimo.equals(emprestimo.getData())) {
                emprestimoEncontrado = true;
                emprestimoEmDetalhe = emprestimo.toString();
            }
        }

        if (!emprestimoEncontrado) {
            throw new EmprestimoInexistenteException("Empréstimo não encontrado!");
        }

        return emprestimoEmDetalhe;
    }

    public Map<LocalDate, Emprestimo> listarEmprestimosCliente(long uidCliente) {
        NavigableMap<LocalDate, Emprestimo> mapaEmprestimos = new TreeMap<>();
        List<Emprestimo> repositorio = this.repoEmprestimo.listar();

        for (Emprestimo emprestimo : repositorio) {
            if(emprestimo.getCliente().getUid() == uidCliente){
                //Preencher mapa
                mapaEmprestimos.put(emprestimo.getData(), emprestimo);
            }
        }
        return mapaEmprestimos;
    }

    public List<Emprestimo> listarComissõesEmprestimo() {
        List<Emprestimo> comissoesEmprestimo = new ArrayList<>();

        return comissoesEmprestimo;
    }

    public Map<LocalDate, Cliente> listarDevedores() {
        NavigableMap<LocalDate, Cliente> mapaClientes = new TreeMap<>();
        List<Emprestimo> repositorio = this.repoEmprestimo.listar();

        for (Emprestimo emprestimo : repositorio) {
            LocalDate prazo = emprestimo.getData().plusDays(emprestimo.getPrazo());
            long dataPagamento = ChronoUnit.DAYS.between(emprestimo.getDataPagamento(), prazo);
            if(dataPagamento < 0){
                //Preencher mapa
                mapaClientes.put(prazo, emprestimo.getCliente());
            }
        }
        return mapaClientes;
    }

    public Map<LocalDate, Cliente> listarDevedoresProtegidos() {
        NavigableMap<LocalDate, Cliente> mapaClientes = new TreeMap<>();
        List<Emprestimo> repositorio = this.repoEmprestimo.listar();

        for (Emprestimo emprestimo : repositorio) {
            LocalDate prazo = emprestimo.getData().plusDays(emprestimo.getPrazo());
            long dataPagamento = ChronoUnit.DAYS.between(emprestimo.getDataPagamento(), prazo);
            if(dataPagamento < 0 /* && score >= ? */){
                //Preencher mapa
                mapaClientes.put(emprestimo, emprestimo.getCliente());
            }
        }
        return mapaClientes;
    }

    public Map<LocalDate, Cliente> listarDevedoresAltoRisco() {
        NavigableMap<LocalDate, Cliente> mapaClientes = new TreeMap<>();
        List<Emprestimo> repositorio = this.repoEmprestimo.listar();

        for (Emprestimo emprestimo : repositorio) {
            LocalDate prazo = emprestimo.getData().plusDays(emprestimo.getPrazo());
            long dataPagamento = ChronoUnit.DAYS.between(emprestimo.getDataPagamento(), prazo);
            if(dataPagamento < 0 /* && score <= ? */){
                //Preencher mapa
                mapaClientes.put(emprestimo, emprestimo.getCliente());
            }
        }
        return mapaClientes;
    }
}
