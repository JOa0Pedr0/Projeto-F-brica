package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

public class ProductionOrderService implements Reportable {

	private ProductionOrderDAO productionOrderDAO = new ProductionOrderDAO();
	private MachineDAO machineDAO = new MachineDAO();
	private EmployeeDAO employeeDAO = new EmployeeDAO();
	private ProductDAO productDAO = new ProductDAO();

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyy hh:mm");

	public void criarNovaOrdem(int produtoId, int quantidade, int maquinaId, int funcionarioId) {

		Product produtoOrdemProducao = productDAO.buscarPorId(produtoId);

		Machine maquinaOrdemProducao = machineDAO.buscarPorId(maquinaId);

		Employee funcionarioOrdemProducao = employeeDAO.buscarPorId(funcionarioId);

		if (quantidade <= 0) {
			throw new BusinessRuleException("A quantidade para uma ordem de produção deve ter um valor acima de 0.");
		}

		if (!(funcionarioOrdemProducao instanceof OperatorMachine)) {
			throw new BusinessRuleException("Não é possível vincular um Gerente a uma ordem de produção, A operação "
					+ "deve ser realizada por um Operador de Máquina.");
		}

		OperatorMachine operadorMaquinaOrdemProducao = (OperatorMachine) funcionarioOrdemProducao;

		if (maquinaOrdemProducao.getStatus() != StatusMachine.OPERANDO) {
			throw new BusinessRuleException(
					"A máquina não se encontra disponível para criar uma nova ordem de produção.");

		}
		ProductionOrder ordemDeProducao = new ProductionOrder(produtoOrdemProducao, quantidade, maquinaOrdemProducao,
				operadorMaquinaOrdemProducao);

		productionOrderDAO.cadastrar(ordemDeProducao);
	}

	public List<ProductionOrder> listarTodas() {
		return productionOrderDAO.listarTodas();
	}

	public ProductionOrder buscarPorId(int id) {
		return productionOrderDAO.buscarPorId(id);
	}

	public void iniciarOrdem(int ordemId) {

		ProductionOrder ordemIniciar = productionOrderDAO.buscarPorId(ordemId);

		if (ordemIniciar.getStatus() != OrderStatus.PENDENTE) {
			throw new BusinessRuleException("Só é possível iniciar uma ordem que está pendente.");
		}
		ordemIniciar.setStatus(OrderStatus.EM_ANDAMENTO);

		productionOrderDAO.atualizar(ordemIniciar);
	}

	public void concluirOrdem(int ordemId) {
		ProductionOrder ordemConcluir = productionOrderDAO.buscarPorId(ordemId);

		if (ordemConcluir.getStatus() != OrderStatus.EM_ANDAMENTO) {
			throw new BusinessRuleException("Só é possível concluir uma ordem que já esteja em andamento.");
		}

		ordemConcluir.setStatus(OrderStatus.CONCLUIDA);

		productionOrderDAO.atualizar(ordemConcluir);
	}

	public void cancelarOrdem(int ordemId) {

		ProductionOrder ordemCancelar = productionOrderDAO.buscarPorId(ordemId);

		if (ordemCancelar.getStatus() != OrderStatus.PENDENTE) {
			throw new BusinessRuleException("Só é possível cancelar uma ordem que esteja pendente.");
		}

		ordemCancelar.setStatus(OrderStatus.CANCELADA);

		productionOrderDAO.atualizar(ordemCancelar);
	}

	@Override
	public String gerarRelatorio() {

		List<ProductionOrder> ordensDeProducao = productionOrderDAO.listarTodas();

		if (ordensDeProducao.isEmpty()) {
			return "Nenhuma Ordem de Produção registrada para gerar relatório.";
		}
		int contPendente = 0;
		int contEmAndamento = 0;
		int contConcluida = 0;
		int contCancelada = 0;

		for (ProductionOrder ordemProducao : ordensDeProducao) {
			switch (ordemProducao.getStatus()) {

			case PENDENTE:
				contPendente++;
				break;
			case EM_ANDAMENTO:
				contEmAndamento++;
				break;
			case CONCLUIDA:
				contConcluida++;
				break;
			case CANCELADA:
				contCancelada++;
				break;
			}

		}
		StringBuilder sb = new StringBuilder();
		sb.append("--- Relatório de Carga de Trabalho ---");
		sb.append("\nData de Geração: ").append(LocalDateTime.now().format(fmt));
		sb.append("\n=======================================================");
		sb.append("\nTotal de Ordens Registradas: ").append(ordensDeProducao.size());

		sb.append("\n\nOrdem de Produção Status:");
		sb.append("\nPENDENTE: ").append(contPendente);
		sb.append("\nEM_ANDAMENTO: ").append(contEmAndamento);
		sb.append("\nCONCULIDA: ").append(contConcluida);
		sb.append("\nCANCELADA ").append(contCancelada);

		sb.append("\n\nVolume de Produção por Produto:");
		Map<String, Integer> volumePorProduto = ordensDeProducao.stream()
				.filter(ordemStatus -> ordemStatus.getStatus() == OrderStatus.CONCLUIDA)
				.collect(Collectors.groupingBy(o -> o.getProdutoASerProduzido().getNome(),
						Collectors.summingInt(ProductionOrder::getQuantidade)));
		if (volumePorProduto.isEmpty() || volumePorProduto.size() == 0) {
			sb.append("\nNo momento sem Ordem de Produção concluída.");
		} else {
			volumePorProduto.forEach((nomeProduto, quantidade) -> sb.append("\n- ").append(nomeProduto).append(": ")
					.append(quantidade).append(" unidades\n"));
		}

		sb.append("\n\nRecursos Atualmente Alocados em Ordens:");

		sb.append("\nMáquinas Alocadas:");
		ordensDeProducao.stream().filter(ordem -> ordem.getStatus() == OrderStatus.EM_ANDAMENTO)
				.map(ordem -> ordem.getMaquinaDesignada()).collect(Collectors.toSet())
				.forEach(maquina -> sb.append("\n- Máquina #").append(maquina.getId()).append(" (")
						.append(maquina.getModelo()).append(")"));

		sb.append("\n\nOperadores Alocados:");

		ordensDeProducao.stream().map(ordem -> ordem.getOperadorResponsavel()).collect(Collectors.toSet())
				.forEach(operador -> sb.append("\n- Operador: ").append(operador.getNome()).append(" (Matrícula: ")
						.append(operador.getMatricula()).append(")"));

		sb.append("\n\n--- Fim do Relatório ---");

		return sb.toString();
	}

}
