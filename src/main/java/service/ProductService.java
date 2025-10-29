package service;

import java.util.List;

import dao.MachineDAO;
import dao.ProductDAO;
import dao.ProductionOrderDAO;
import entities.Machine;
import entities.Product;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

public class ProductService implements Reportable {

	private ProductDAO productDAO = new ProductDAO();
	private MachineDAO machineDAO = new MachineDAO();
	private ProductionOrderDAO productionOrderDAO = new ProductionOrderDAO();

	public void adicionarProduto(String nome, String descricao, double preco, int maquinaId) {

		Machine maquina = machineDAO.buscarPorId(maquinaId);

		if (preco <= 0) {
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		Product produto = new Product(preco, nome, descricao, maquina);

		productDAO.cadastrar(produto);

	}

	public List<Product> listarTodosProdutos() {
		return productDAO.listarTodas();
	}

	public Product buscarPorId(int id) {
		return productDAO.buscarPorId(id);
	}

	public void atualizar(int produtoId, String nome, String descricao, double preco, int maquinaId) {

		Product produtoAtualizar = productDAO.buscarPorId(produtoId);

		Machine maquinaAttProduto = machineDAO.buscarPorId(maquinaId);

		if (preco <= 0) {
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		produtoAtualizar.setNome(nome);
		produtoAtualizar.setDescricao(descricao);
		produtoAtualizar.setPrecoCusto(preco);
		produtoAtualizar.setMaquina(maquinaAttProduto);

		productDAO.atualizar(produtoAtualizar);

	}

	public void remover(int id) {
		Product produtoRemover = productDAO.buscarPorId(id);
		
		boolean historicoProduto = productionOrderDAO.existeOrdemComProdutoId(id);
		if(historicoProduto) {
			throw new BusinessRuleException("Não é possível remover um produto que tem histórico em uma ordem de produção.");
		}

		productDAO.remover(produtoRemover);
	}

	@Override
	public String gerarRelatorio() {

		List<Product> produtosEmEstoque = productDAO.listarTodas();

		if (produtosEmEstoque.isEmpty()) {
			return "Estoque vazio. Nenhum relatório a ser gerado.";
		}

		double valorTotalEmEstoque = produtosEmEstoque.stream().mapToDouble(Product::getPrecoCusto).sum();

		return "Quantidade de produtos no estoque: " + produtosEmEstoque.size() + "\nValor do estoque: R$ "
				+ valorTotalEmEstoque;
	}
}
