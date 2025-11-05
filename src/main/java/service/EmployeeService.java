package service;

import java.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.EmployeeDAO;
import dao.MachineDAO;
import dao.ProductionOrderDAO;
import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

@Service
public class EmployeeService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeDAO employeeDAO;

	private MachineDAO machineDAO = new MachineDAO();
	private ProductionOrderDAO productionOrderDAO = new ProductionOrderDAO();

	public void adicionarGerente(String nome, String matricula, String areaResponsavel) {
		logger.debug("Iniciado cadastro de gerente.");

		Manager novogerente = new Manager(nome, matricula, areaResponsavel);

		employeeDAO.cadastrar(novogerente);
		logger.info("Cadastro de gerente concluído com sucesso.");
	}

	public void adicionarOperadorDeMaquina(String nome, String matricula, int maquinaId) {
		logger.debug("Inciando cadastro de Operador de Máquina.");

		if (machineDAO.listarTodos().isEmpty()) {
			logger.warn("Tentativa de cadastrar operador falhou: Nenhuma máquina registrada.");
			throw new BusinessRuleException(
					"O Operador de máquina não pode ser cadastrado sem a existência de máquinas no sistema.");
		}
		Machine maquina = machineDAO.buscarPorId(maquinaId);

		OperatorMachine novoOperadorMaquina = new OperatorMachine(nome, matricula, maquina);

		employeeDAO.cadastrar(novoOperadorMaquina);
		logger.info("Operador de máquina adicionado com sucesso.");

	}

	public List<Employee> listarTodosFuncionarios() {
		logger.debug("Inciado Listagem de funcionários.");
		return employeeDAO.listarTodas();
	}

	public Employee buscarPorId(int id) {
		logger.debug("Tentativa de busca de funcionário ID: {}.", id);
		return employeeDAO.buscarPorId(id);
	}

	public void atualizarGerente(int id, String nome, String arearResponsavel) {
		logger.debug("Inciando atualização do funcionário ID: {}", id);
		Employee funcionario = employeeDAO.buscarPorId(id);

		if (!(funcionario instanceof Manager)) {
			logger.warn("Falha ao tentar atualizar: ID {} não é um Gerente", id);
			throw new BusinessRuleException("O ID " + id + " não pertence a um Gerente.");
		}

		Manager gerente = (Manager) funcionario;
		gerente.setNome(nome);
		gerente.setAreaResponsavel(arearResponsavel);

		employeeDAO.atualizar(gerente);
		logger.info("Gerente ID: {} atualizado com sucesso.", id);
	}

	public void atualizarOperadorDeMaquina(int operadorMaquinaId, int maquinaId, String nome) {
		logger.debug("Inciando atualização do funcionário ID: {}", operadorMaquinaId);
		Employee funcionario = employeeDAO.buscarPorId(operadorMaquinaId);
		if (!(funcionario instanceof OperatorMachine)) {
			logger.warn("Falha ao tentar atualizar: ID {} não é um Operador de Máquina", operadorMaquinaId);
			throw new BusinessRuleException("O ID " + operadorMaquinaId + " não pertence a um Operador de Máquina.");
		}
		OperatorMachine operadorMaquinaAtt = (OperatorMachine) funcionario;

		Machine maquina = machineDAO.buscarPorId(maquinaId);

		operadorMaquinaAtt.setNome(nome);
		operadorMaquinaAtt.setMaquinaAlocada(maquina);

		employeeDAO.atualizar(operadorMaquinaAtt);
		logger.info(" Operador de Máquina ID: {} atualizado com suceso", operadorMaquinaId);
	}

	public void remover(int id) {
		logger.debug("Tentativa de remoção de funcionário ID: {}.", id);
		Employee funcionarioRemover = employeeDAO.buscarPorId(id);
		logger.debug("Validando a instancia do funcionário.");
		if (funcionarioRemover instanceof OperatorMachine) {
			logger.debug("Verificando se o funcionário tem registro em alguma Ordem de Produção.");
			boolean operadorHistorico = productionOrderDAO.existeOrdemComOperadorId(id);

			if (operadorHistorico) {
				logger.warn("Tentativa inválida de remover funcionário vinculado a uma Ordem de Produção.");
				throw new BusinessRuleException(
						"Não é possível remover um operador que tenha histórico em uma ordem de produção.");
			}
		}

		employeeDAO.remover(funcionarioRemover);
	}

	@Override
	public String gerarRelatorio() {
		logger.debug("Inciando geração de relatório.");
		List<Employee> funcionarios = employeeDAO.listarTodas();
		if (funcionarios.isEmpty()) {
			logger.warn("Operação falhou. Não há registro de funcionários no Banco de Dados");
			return "Não há funcionários cadastrados para emitir relatório.";
		}
		int contadorGerentes = 0;
		int contadorOperador = 0;
		int operadoresAtivos = 0;
		int operadoresInativos = 0;
		int operadoresSemMaquina = 0;

		StringBuilder sb = new StringBuilder();

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").withZone(ZoneId.systemDefault());

		for (Employee emp : funcionarios) {
			if (emp instanceof Manager) {
				contadorGerentes++;
			} else if (emp instanceof OperatorMachine) {
				contadorOperador++;
				OperatorMachine op = (OperatorMachine) emp;

				if (op.getMaquinaAlocada() != null) {
					switch (op.getMaquinaAlocada().getStatus()) {
					case OPERANDO:
						operadoresAtivos++;
						break;
					case EM_MANUTENCAO:
						operadoresInativos++;
						break;
					case PARADA:
						operadoresInativos++;
						break;
					}
				} else {
					operadoresSemMaquina++;
				}
			}
		}
		sb.append("--- Relatório de Funcionários ---\n");
		sb.append("Data de Geração:").append(fmt.format(LocalDateTime.now()));
		sb.append("\n\n==================================").append("\nResumo Geral:\n");
		sb.append("- Total de funcionários: ").append(funcionarios.size());
		sb.append("\n- Gerentes: ").append(contadorGerentes);
		sb.append("\n- Operadores de máquinas: ").append(contadorOperador);
		sb.append("\n\nSituação dos operadores: ");
		sb.append("\n- Operadores em máquinas ativas (OPERANDO): ").append(operadoresAtivos);
		sb.append("\n- Operadores em máquinas inativas (PARADA/MANUTENÇÃO): ").append(operadoresInativos);
		sb.append("\n- Operadores sem máquina alocada: ").append(operadoresSemMaquina);
		sb.append("\n\nGerência por área:");
		for (Employee emp : funcionarios) {
			if (emp instanceof Manager) {
				Manager gerente = (Manager) emp;
				sb.append("\n- ").append(gerente);
				sb.append("(Área : ").append(gerente.getAreaResponsavel()).append(")\n");
			}
		}
		sb.append("\n--- Fim do relatório ---");
		logger.info("Relatório gerado com sucesso.");
		return sb.toString();
	}

}
