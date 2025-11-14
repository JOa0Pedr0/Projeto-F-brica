package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.MachineDAO;
import dao.ProductDAO;
import dao.ProductionOrderDAO;
import entities.Machine;
import entities.Product;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

@Service
public class ProductService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ProductDAO productDAO;
	@Autowired
	private MachineDAO machineDAO;
	@Autowired
	private ProductionOrderDAO productionOrderDAO;

	public Product adicionarProduto(Product produto) {
		logger.debug("Iniciando cadastro de novo produto. Nome: {}", produto.getNome());

		if (produto.getPrecoCusto() <= 0) {
			logger.warn("Tentativa de cadastrar produto '{}' com preço inválido ({}).", produto.getNome(),
					produto.getPrecoCusto());
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		if (produto.getMaquina() == null || produto.getMaquina().getId() == 0) {
			logger.warn("Tentativa de cadastrar produto '{}' em uma máquina válida.", produto.getNome());
			throw new BusinessRuleException("A máquina informada não é válida.");
		}

		productDAO.cadastrar(produto);
		return produto;

	}

	public List<Product> listarTodosProdutos() {
		logger.debug("Buscando lista de todos os produtos.");
		return productDAO.listarTodas();
	}

	public Product buscarPorId(int id) {
		logger.debug("Buscando produto por ID: {}", id);

		return productDAO.buscarPorId(id);
	}

	public Product atualizar(int produtoId, Product novosDados) {
		logger.debug("Iniciando atualização do produto ID: {}", produtoId);

		Product produtoAtualizar = productDAO.buscarPorId(produtoId);
		Machine maquinaAttProduto = machineDAO.buscarPorId(novosDados.getMaquina().getId());

		if (novosDados.getPrecoCusto() <= 0) {
			logger.warn("Tentativa de atualizar produto ID: {} com preço inválido ({}).", produtoId,
					novosDados.getPrecoCusto());
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}

		produtoAtualizar.setNome(novosDados.getNome());
		produtoAtualizar.setDescricao(novosDados.getDescricao());
		produtoAtualizar.setPrecoCusto(novosDados.getPrecoCusto());
		produtoAtualizar.setMaquina(maquinaAttProduto);

		Product produtoAtualizado = productDAO.atualizar(produtoAtualizar);
		return produtoAtualizado;
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

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puro para gerar relatório.");
		List<Product> produtos = productDAO.listarTodas();

		if (produtos.isEmpty()) {
			logger.warn("Não há dados para o relatório.");
			throw new BusinessRuleException("Nenhum produto cadastrado para gerar relatório.");
		}

		double valorEstoque = produtos.stream().mapToDouble(Product::getPrecoCusto).sum();

		Map<String, Object> dadosRelatorio = new HashMap<>();

		dadosRelatorio.put("QuantidadeDeProdutos", produtos.size());
		dadosRelatorio.put("PrecoEstoqueEstimado", valorEstoque);

		logger.info("Dados para relatório coletados com sucesso.");
		return dadosRelatorio;
	}

	@Override
	public String gerarRelatorio() {
		try {
			Map< String, Object> dados = obterDadosRelatorio();
			StringBuilder sb = new StringBuilder();
			
			sb.append("Quantidade de produtos registrados:").append(dados.get("QuantidadeDeProdutos"));
			sb.append("Preço estimado do estoque: ").append(dados.get("PrecoEstoqueEstimado"));
			
			return sb.toString();
		} catch (BusinessRuleException e) {
			logger.warn("Operação falhou: {}", e.getMessage());
			return e.getMessage();
		}

	}
}