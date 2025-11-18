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
import service.exceptions.ResourceNotFoundException;

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

		if (ordemRecebidaDoJSON.getQuantidade() <= 0) {
			logger.warn("Quantidade inválida.");
			throw new BusinessRuleException("A quantidade para uma ordem de produção deve ter um valor acima de 0.");
		}
		
		int maquinaId = ordemRecebidaDoJSON.getMaquinaDesignada().getId();
		int produtoId = ordemRecebidaDoJSON.getProdutoASerProduzido().getId();
		int operadorId = ordemRecebidaDoJSON.getOperadorResponsavel().getId();
		
		Employee funcionario = employeeDAO.findById(operadorId).orElseThrow(() -> {
			logger.warn("Tentativa inválida de adicionar um Operador de Máquina inexistente.");
			throw new ResourceNotFoundException("");
		});
		
		if(!(funcionario instanceof OperatorMachine)) {
			logger.warn("Tentativa de adicionar Gerente em uma Operação de Operador.");
			throw new BusinessRuleException("Não é possível vincular um Gerente a uma ordem de produção.");
		}
		
		OperatorMachine operador = (OperatorMachine) funcionario;
		
		Product produtoOrdem = productDAO.findById(produtoId).orElseThrow(() -> {
			logger.warn("Tentativa inválida de adicionar um produto inexistente.");
			throw new ResourceNotFoundException("O produto com ID: " + produtoId + " não existe");
		});
		
		Machine maquinaOrdem = machineDAO.findById(maquinaId).orElseThrow(() -> {
			logger.warn("Tentativa inválida de adicionar uma máquina inexistente.");
			throw new ResourceNotFoundException("A máquina com ID: " + maquinaId + " não existe.");	
		});

		if (maquinaOrdem.getStatus() != StatusMachine.OPERANDO) {
			logger.warn("Tentativa de adicionar máquina com status: {}",
					maquinaOrdem.getStatus());
			throw new BusinessRuleException("A máquina não se encontra disponível (Status: "
					+ maquinaOrdem.getStatus() + ").");
		}

		ProductionOrder novaOrdem = new ProductionOrder(produtoOrdem, ordemRecebidaDoJSON.getQuantidade(), maquinaOrdem, operador);
		ProductionOrder ordemSalva = productionOrderDAO.save(novaOrdem);

		logger.info("Nova Ordem de Produção criada com sucesso. ID: {}", ordemSalva.getId());
		return ordemSalva;
	}

	public List<ProductionOrder> listarTodas() {
		logger.debug("Inciando buscas de Ordens de Produções.");
		return productionOrderDAO.findAll();
	}

	public ProductionOrder buscarPorId(int id) {
		logger.debug("Iniciando busca para Ordem de Produção ID: {}.", id);
		return productionOrderDAO.findById(id).orElseThrow(() -> {
			logger.warn("Ordem de produção com ID: {} não encontrada.", id);
			return new ResourceNotFoundException("Ordem de produção com ID:" + id + " não encontrada.");
		});
	}

	public ProductionOrder iniciarOrdem(int ordemId) {
		logger.debug("Inciado uma Ordem de Produção ID: {}.", ordemId);
		ProductionOrder ordemIniciar = this.buscarPorId(ordemId);

		if (ordemIniciar.getStatus() != OrderStatus.PENDENTE) {
			logger.warn("Operação falhou. Tentativa de iniciar uma Ordem de Operação que não esteja PENDENTE.");
			throw new BusinessRuleException("Só é possível iniciar uma ordem que está pendente.");
		}
		ordemIniciar.setStatus(OrderStatus.EM_ANDAMENTO);
		ProductionOrder ordemAtt = productionOrderDAO.save(ordemIniciar);
		logger.info("Ordem de Produção ID: {} iniciada com sucesso.", ordemId);
		return ordemAtt;
	}

	public ProductionOrder concluirOrdem(int ordemId) {
		logger.debug("Iniando conclusão de Ordem de Produção ID: {}.", ordemId);

		ProductionOrder ordemConcluir = this.buscarPorId(ordemId);

		if (ordemConcluir.getStatus() != OrderStatus.EM_ANDAMENTO) {
			logger.warn("Operação falhou. Tentativa de concluir uma Ordem de Operação que não esteja EM_ANDAMENTO.");
			throw new BusinessRuleException("Só é possível concluir uma ordem que já esteja em andamento.");
		}

		ordemConcluir.setStatus(OrderStatus.CONCLUIDA);
		ProductionOrder ordemAtt = productionOrderDAO.save(ordemConcluir);
		logger.info("Ordem de Produção concluída com sucesso ID: {}.", ordemId);
		return ordemAtt;
	}

	public ProductionOrder cancelarOrdem(int ordemId) {
		logger.debug("Inicando cancelamento de Ordem de Produção ID: {}", ordemId);
		ProductionOrder ordemCancelar = this.buscarPorId(ordemId);

		if (ordemCancelar.getStatus() != OrderStatus.PENDENTE) {
			logger.warn("Operação falhou. Tentativa de cancelar uma Ordem de Produção que não esteja PENDENTE.");
			throw new BusinessRuleException("Só é possível cancelar uma ordem que esteja pendente.");
		}

		ordemCancelar.setStatus(OrderStatus.CANCELADA);
		ProductionOrder ordemAtt = productionOrderDAO.save(ordemCancelar);
		logger.info("Ordem de Produção cancelada com sucesso ID: {}.", ordemId);
		return ordemAtt;
	}

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puros para o relatório.");
		List<ProductionOrder> ordensDeProducao = productionOrderDAO.findAll();

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
