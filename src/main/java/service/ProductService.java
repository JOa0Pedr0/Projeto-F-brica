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
import service.exceptions.ResourceNotFoundException;

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

		int maquinaId = produto.getMaquina().getId();

		Machine maquinaVinculada = machineDAO.findById(maquinaId).orElseThrow(() -> {
			logger.warn("Tentativa de vincular uma máquina inexistente em um novo produto.");
			throw new ResourceNotFoundException("Máquina com ID: " + maquinaId + " não existe.");
		});

		Product produtoSalvo = new Product(produto.getPrecoCusto(), produto.getNome(), produto.getDescricao(),
				maquinaVinculada);
		Product novoProduto = productDAO.save(produtoSalvo);
		return novoProduto;

	}

	public List<Product> listarTodosProdutos() {
		logger.debug("Buscando lista de todos os produtos.");
		return productDAO.findAll();
	}

	public Product buscarPorId(int id) {
		logger.debug("Buscando produto por ID: {}", id);

		return productDAO.findById(id).orElseThrow(() -> {
			logger.warn("Produto com ID: {} não encontrado.", id);
			return new ResourceNotFoundException("Produto não encontrado para o ID:" + id);
		});
	}

	public Product atualizar(int produtoId, Product novosDados) {
		logger.debug("Iniciando atualização do produto ID: {}", produtoId);

		Product produtoAtualizar = this.buscarPorId(produtoId);

		if (novosDados.getPrecoCusto() <= 0) {
			logger.warn("Tentativa de atualizar produto ID: {} com preço inválido ({}).", produtoId,
					novosDados.getPrecoCusto());
			throw new BusinessRuleException("O preço informado deve ser maior que 0.");
		}
		
		int maquinaId = novosDados.getMaquina().getId();
		
		Machine novaMaquina = machineDAO.findById(maquinaId).orElseThrow(() ->  {
			logger.warn("Tetativa inválida de atualizar produto com máquina inexistente.");
			throw new ResourceNotFoundException("Máquina com ID: " + maquinaId + " não existe.");
		});

		produtoAtualizar.setNome(novosDados.getNome());
		produtoAtualizar.setDescricao(novosDados.getDescricao());
		produtoAtualizar.setPrecoCusto(novosDados.getPrecoCusto());
		produtoAtualizar.setMaquina(novaMaquina);

		Product produtoAtualizado = productDAO.save(produtoAtualizar);
		return produtoAtualizado;
	}

	public void remover(int id) {
		logger.debug("Iniciando remoção do produto ID: {}", id);
		Product produtoRemover = this.buscarPorId(id);

		logger.debug("Verificando histórico de ordens para o produto ID: {}", id);
		Long contagem = productionOrderDAO.countByProdutoId(id);

		boolean historicoProduto = contagem > 0;
		if (historicoProduto) {
			logger.warn("Tentativa de remover produto ID: {} falhou. Produto está vinculado a uma  Ordem de Produção.", id);
			throw new BusinessRuleException(
					"Não é possível remover um produto que tem histórico em uma ordem de produção.");
		}

		productDAO.delete(produtoRemover);

	}

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puro para gerar relatório.");
		List<Product> produtos = productDAO.findAll();

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
			Map<String, Object> dados = obterDadosRelatorio();
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