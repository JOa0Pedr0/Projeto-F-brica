package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.EmployeeDAO;
import dao.MachineDAO;
import dao.ProductDAO;
import dao.ProductionOrderDAO;
import entities.Employee;
import entities.Machine;
import entities.OperatorMachine;
import entities.OrderStatus;
import entities.Product;
import entities.ProductionOrder;
import entities.StatusMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

@Service
public class ProductionOrderService implements Reportable {


	@Autowired
	private ProductionOrderDAO productionOrderDAO;
	
	@Autowired
	private MachineDAO machineDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProductDAO productDAO;

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private static final Logger logger = LoggerFactory.getLogger(ProductionOrderService.class);

	public ProductionOrder criarNovaOrdem(ProductionOrder ordemRecebidaDoJSON) {
		logger.debug("Inciando criação de Ordem de Produção.");

		Product produtoReal = productDAO.buscarPorId(ordemRecebidaDoJSON.getProdutoASerProduzido().getId());
		Machine maquinaReal = machineDAO.buscarPorId(ordemRecebidaDoJSON.getMaquinaDesignada().getId());
		Employee funcionarioReal = employeeDAO.buscarPorId(ordemRecebidaDoJSON.getOperadorResponsavel().getId());

		if (ordemRecebidaDoJSON.getQuantidade() <= 0) {
			logger.warn("Quantidade inválida.");
			throw new BusinessRuleException("A quantidade para uma ordem de produção deve ter um valor acima de 0.");
		}

		if (!(funcionarioReal instanceof OperatorMachine)) {
			logger.warn("Tentativa de adicionar Gerente em uma Operação de Operador.");
			throw new BusinessRuleException("Não é possível vincular um Gerente a uma ordem de produção.");
		}

		if (maquinaReal.getStatus() != StatusMachine.OPERANDO) {
			logger.warn("Tentativa de adicionar máquina com status: {}", maquinaReal.getStatus());
			throw new BusinessRuleException(
					"A máquina não se encontra disponível (Status: " + maquinaReal.getStatus() + ").");
		}

		OperatorMachine operadorReal = (OperatorMachine) funcionarioReal;

		ProductionOrder ordemDeProducaoFinal = new ProductionOrder(produtoReal, ordemRecebidaDoJSON.getQuantidade(),
				maquinaReal, operadorReal);

		productionOrderDAO.cadastrar(ordemDeProducaoFinal);
		logger.info("Nova Ordem de Produção criada com sucesso. ID: {}", ordemDeProducaoFinal.getId());
		return ordemDeProducaoFinal;
	}

	public List<ProductionOrder> listarTodas() {
		logger.debug("Inciando buscas de Ordens de Produções.");
		return productionOrderDAO.listarTodas();
	}

	public ProductionOrder buscarPorId(int id) {
		logger.debug("Iniciando busca para Ordem de Produção ID: {}.", id);
		return productionOrderDAO.buscarPorId(id);
	}

	public ProductionOrder iniciarOrdem(int ordemId) {
		logger.debug("Inciado uma Ordem de Produção ID: {}.", ordemId);
		ProductionOrder ordemIniciar = productionOrderDAO.buscarPorId(ordemId);

		if (ordemIniciar.getStatus() != OrderStatus.PENDENTE) {
			logger.warn("Operação falhou. Tentativa de iniciar uma Ordem de Operação que não esteja PENDENTE.");
			throw new BusinessRuleException("Só é possível iniciar uma ordem que está pendente.");
		}
		ordemIniciar.setStatus(OrderStatus.EM_ANDAMENTO);

		productionOrderDAO.atualizar(ordemIniciar);
		logger.info("Ordem de Produção ID: {} iniciada com sucesso.", ordemId);
		return ordemIniciar;
	}

	public ProductionOrder concluirOrdem(int ordemId) {
		logger.debug("Iniando conclusão de Ordem de Produção ID: {}.", ordemId);

		ProductionOrder ordemConcluir = productionOrderDAO.buscarPorId(ordemId);

		if (ordemConcluir.getStatus() != OrderStatus.EM_ANDAMENTO) {
			logger.warn("Operação falhou. Tentativa de concluir uma Ordem de Operação que não esteja EM_ANDAMENTO.");
			throw new BusinessRuleException("Só é possível concluir uma ordem que já esteja em andamento.");
		}

		ordemConcluir.setStatus(OrderStatus.CONCLUIDA);

		productionOrderDAO.atualizar(ordemConcluir);
		logger.info("Ordem de Produção concluída com sucesso ID: {}.", ordemId);
		return ordemConcluir;
	}

	public ProductionOrder cancelarOrdem(int ordemId) {
		logger.debug("Inicando cancelamento de Ordem de Produção ID: {}", ordemId);
		ProductionOrder ordemCancelar = productionOrderDAO.buscarPorId(ordemId);

		if (ordemCancelar.getStatus() != OrderStatus.PENDENTE) {
			logger.warn("Operação falhou. Tentativa de cancelar uma Ordem de Produção que não esteja PENDENTE.");
			throw new BusinessRuleException("Só é possível cancelar uma ordem que esteja pendente.");
		}

		ordemCancelar.setStatus(OrderStatus.CANCELADA);

		productionOrderDAO.atualizar(ordemCancelar);
		logger.info("Ordem de Produção cancelada com sucesso ID: {}.", ordemId);
		return ordemCancelar;
	}

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puros para o relatório.");
		List<ProductionOrder> ordensDeProducao = productionOrderDAO.listarTodas();

		if (ordensDeProducao.isEmpty()) {
			logger.warn("Não há dados para relatório.");
			throw new BusinessRuleException("Nenhuma Ordem de Produção registrada para gerar relatório.");
		}

		long contPendente = ordensDeProducao.stream().filter(o -> o.getStatus() == OrderStatus.PENDENTE).count();
		long contEmAndamento = ordensDeProducao.stream().filter(o -> o.getStatus() == OrderStatus.EM_ANDAMENTO).count();
		long contCancelado = ordensDeProducao.stream().filter(o -> o.getStatus() == OrderStatus.CANCELADA).count();

		Map<String, Integer> volumeConcluido = ordensDeProducao.stream()
				.filter(o -> o.getStatus() == OrderStatus.CONCLUIDA)
				.collect(Collectors.groupingBy(o -> o.getProdutoASerProduzido().getNome(),
						Collectors.summingInt(ProductionOrder::getQuantidade)));

		Set<Machine> maquinasAtivas = ordensDeProducao.stream()
				.filter(ordem -> ordem.getStatus() == OrderStatus.EM_ANDAMENTO)
				.map(ordem -> ordem.getMaquinaDesignada()).collect(Collectors.toSet()); 

		Set<OperatorMachine> operadoresAtivos = ordensDeProducao.stream()
				.filter(ordem -> ordem.getStatus() == OrderStatus.EM_ANDAMENTO)
				.map(ordem -> ordem.getOperadorResponsavel()).collect(Collectors.toSet());

		Map<String, Object> dadosRelatorio = new HashMap<>();
		dadosRelatorio.put("totalOrdens", ordensDeProducao.size()); 
		dadosRelatorio.put("statusPendente", contPendente);
		dadosRelatorio.put("statusEmAndamento", contEmAndamento);
		dadosRelatorio.put("volumeProducaoConcluido", volumeConcluido);
		dadosRelatorio.put("statusCancelado", contCancelado);
		dadosRelatorio.put("maquinasEmAndamento", maquinasAtivas);
		dadosRelatorio.put("operadoresAtivos", operadoresAtivos);

		logger.info("Dados para relatório coletados com sucesso.");
		return dadosRelatorio;

	}

	@Override
	public String gerarRelatorio() {
		try {
			Map<String, Object> dados = obterDadosRelatorio();

			StringBuilder sb = new StringBuilder();

			sb.append("--- Relatório de Carga de Trabalho ---");
			sb.append("\nData de Geração: ").append(LocalDateTime.now().format(fmt));

			sb.append("\n=======================================================");
			sb.append("\nTotal de Ordens Registradas: ").append(dados.get("totalOrdens"));

			sb.append("\n\nOrdem de Produção Status:");
			sb.append("\nPENDENTE: ").append(dados.get("statusPendente"));
			sb.append("\nEM_ANDAMENTO: ").append(dados.get("statusEmAndamento"));
			sb.append("\nCONCLUIDA: ").append(dados.get("contConcluida"));
			sb.append("\nCANCELADA ").append(dados.get("statusCancelado"));

			sb.append("\n\n--- Fim do Relatório ---");

			return sb.toString();

		} catch (BusinessRuleException e) {
			logger.warn("Operação falhou: {}", e.getMessage());
			return e.getMessage();
		}
	}
}
