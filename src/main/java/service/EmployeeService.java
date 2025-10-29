package service;

import java.time.LocalDateTime;
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

public class EmployeeService implements Reportable {

	private EmployeeDAO employeeDAO = new EmployeeDAO();
	private MachineDAO machineDAO = new MachineDAO();
	private ProductionOrderDAO productionOrderDAO = new ProductionOrderDAO();

	public void adicionarGerente(String nome, String matricula, String areaResponsavel) {
		Manager novogerente = new Manager(nome, matricula, areaResponsavel);

		employeeDAO.cadastrar(novogerente);
	}

	public void adicionarOperadorDeMaquina(String nome, String matricula, int maquinaId) {

		Machine maquina = machineDAO.buscarPorId(maquinaId);

		OperatorMachine novoOperadorMaquina = new OperatorMachine(nome, matricula, maquina);

		employeeDAO.cadastrar(novoOperadorMaquina);

	}

	public List<Employee> listarTodosFuncionarios() {
		return employeeDAO.listarTodas();
	}

	public Employee buscarPorId(int id) {
		return employeeDAO.buscarPorId(id);
	}

	public void atualizarGerente(int id, String nome, String arearResponsavel) {
		Employee funcionario = employeeDAO.buscarPorId(id);

		if (!(funcionario instanceof Manager)) {
			throw new BusinessRuleException("O ID " + id + " não pertence a um Gerentte.");
		}

		Manager gerente = (Manager) funcionario;
		gerente.setNome(nome);
		gerente.setAreaResponsavel(arearResponsavel);

		employeeDAO.atualizar(gerente);
	}

	public void atualizarOperadorDeMaquina(int operadorMaquinaId, int maquinaId, String nome) {
		Employee funcionario = employeeDAO.buscarPorId(operadorMaquinaId);
		if (!(funcionario instanceof OperatorMachine)) {
			throw new BusinessRuleException("O ID " + operadorMaquinaId + " não pertence a um Operador de Máquina.");
		}
		OperatorMachine operadorMaquinaAtt = (OperatorMachine) funcionario;

		Machine maquina = machineDAO.buscarPorId(maquinaId);

		operadorMaquinaAtt.setNome(nome);
		operadorMaquinaAtt.setMaquinaAlocada(maquina);

		employeeDAO.atualizar(operadorMaquinaAtt);
	}

	public void remover(int id) {
		Employee funcionarioRemover = employeeDAO.buscarPorId(id);
		if (funcionarioRemover instanceof OperatorMachine) {
			boolean operadorHistorico = productionOrderDAO.existeOrdemComOperadorId(id);

			if (operadorHistorico) {
				throw new BusinessRuleException(
						"Não é possível remover um operador que tenha histórico em uma ordem de produção.");
			}
		}

		employeeDAO.remover(funcionarioRemover);
	}

	@Override
	public String gerarRelatorio() {
		List<Employee> funcionarios = employeeDAO.listarTodas();
		if (funcionarios.isEmpty()) {
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
		return sb.toString();
	}

}
