package service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import entities.Employee;
import entities.Manager;
import entities.OperatorMachine;
import interfaces.Reportable;
import service.exceptions.ResourceNotFoundException;

public class EmployeeService implements Reportable {
	private List<Employee> employees = new ArrayList<>();

	public void adicionarFuncionario(Employee funcionario) {
		if (funcionario == null) {
			throw new IllegalArgumentException("Não é possível registrar um funcionário nulo.");
		}
		employees.add(funcionario);
		System.out.println("Funcionário cadastrado com sucesso.");
	}
	
	public List<Employee> listarTodosFuncionarios() {
		return employees;
	}
	

	public Employee buscarPorId(int id) {
		for (Employee employee : employees) {
			if (employee.getId() == id) {
				return employee;
			}
		}
		throw new ResourceNotFoundException("Funcionário não encontrado. Id: " + id);
	}

	@Override
	public String gerarRelatorio() {
		if (employees.isEmpty()) {
			return "Não há funcionários cadastrados para emitir relatório.";
		}
		int contadorGerentes = 0;
		int contadorOperador = 0;
		int operadoresAtivos = 0;
		int operadoresInativos = 0;
		int operadoresSemMaquina = 0;

		StringBuilder sb = new StringBuilder();

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").withZone(ZoneId.systemDefault());
		for (Employee emp : employees) {
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
					case EM_MANUTENCAO, PARADA:
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
		sb.append("- Total de funcionários: ").append(employees.size());
		sb.append("\n- Gerentes: ").append(contadorGerentes);
		sb.append("\n- Operadores de máquinas: ").append(contadorOperador);
		sb.append("\n\nSituação dos operadores: ");
		sb.append("\n- Operadores em máquinas ativas (OPERANDO): ").append(operadoresAtivos);
		sb.append("\n- Operadores em máquinas inativas (PARADA/MANUTENÇÃO): ").append(operadoresInativos);
		sb.append("\n- Operadores sem máquina alocada: ").append(operadoresSemMaquina);
		sb.append("\n\nGerência por área:");
		for (Employee emp : employees) {
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
