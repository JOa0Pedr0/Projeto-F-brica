package service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dao.MachineDAO;
import dao.ProductDAO;
import dao.ProductionOrderDAO;
import entities.Machine;
import entities.Product;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

public class ProductService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	private ProductDAO productDAO = new ProductDAO();
	private MachineDAO machineDAO = new MachineDAO();
	private ProductionOrderDAO productionOrderDAO = new ProductionOrderDAO();

	public void adicionarProduto(String nome, String descricao, double preco, int maquinaId) {
		logger.debug("Iniciando cadastro de novo produto. Nome: {}", nome);

		Machine maquina = machineDAO.buscarPorId(maquinaId);

		if (preco <= 0) {
			logger.warn("Tentativa de cadastrar produto '{}' com preço inválido ({}).", nome, preco);
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		Product produto = new Product(preco, nome, descricao, maquina);
		productDAO.cadastrar(produto);

	}

	public List<Product> listarTodosProdutos() {
		logger.debug("Buscando lista de todos os produtos.");
		return productDAO.listarTodas();
	}

	public Product buscarPorId(int id) {
		logger.debug("Buscando produto por ID: {}", id);

		return productDAO.buscarPorId(id);
	}

	public void atualizar(int produtoId, String nome, String descricao, double preco, int maquinaId) {
		logger.debug("Iniciando atualização do produto ID: {}", produtoId);

		Product produtoAtualizar = productDAO.buscarPorId(produtoId);
		Machine maquinaAttProduto = machineDAO.buscarPorId(maquinaId);

		if (preco <= 0) {
			logger.warn("Tentativa de atualizar produto ID: {} com preço inválido ({}).", produtoId, preco);
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		produtoAtualizar.setNome(nome);
		
		produtoAtualizar.setDescricao(descricao);
		produtoAtualizar.setPrecoCusto(preco);
		produtoAtualizar.setMaquina(maquinaAttProduto);

		productDAO.atualizar(produtoAtualizar);
	}

	public void remover(int id) {
		logger.debug("Iniciando remoção do produto ID: {}", id);
		Product produtoRemover = productDAO.buscarPorId(id);

		logger.debug("Verificando histórico de ordens para o produto ID: {}", id);
		boolean historicoProduto = productionOrderDAO.existeOrdemComProdutoId(id);
		if (historicoProduto) {
			logger.warn("Tentativa de remover produto ID: {} falhou. Produto está em uso em ordens de produção.", id);
			throw new BusinessRuleException(
					"Não é possível remover um produto que tem histórico em uma ordem de produção.");
		}

		productDAO.remover(produtoRemover);
		
	}

	@Override
	public String gerarRelatorio() {
		logger.debug("Iniciando geração de relatório de estoque de produtos.");
		List<Product> produtosEmEstoque = productDAO.listarTodas();

		if (produtosEmEstoque.isEmpty()) {
			logger.warn("Geração de relatório de produtos falhou: estoque vazio.");
			return "Estoque vazio. Nenhum relatório a ser gerado.";
		}

		double valorTotalEmEstoque = produtosEmEstoque.stream().mapToDouble(Product::getPrecoCusto).sum();

		logger.info("Relatório de estoque de produtos gerado com sucesso.");
		return "Quantidade de produtos no estoque: " + produtosEmEstoque.size() + "\nValor do estoque: R$ "
				+ String.format("%.2f", valorTotalEmEstoque);
	}
}