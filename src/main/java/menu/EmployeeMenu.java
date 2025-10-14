package menu;

import java.util.List;
import java.util.Scanner;

import entities.Employee;
import entities.Machine;
import entities.Manager;
import service.EmployeeService;
import service.MachineService;

public class EmployeeMenu {

	private EmployeeService employeeService;
	private MachineService machineService;
	Scanner sc;

	public EmployeeMenu(EmployeeService employeeService, MachineService machineService, Scanner sc) {
		this.employeeService = employeeService;
		this.machineService = machineService;
		this.sc = sc;
	}

	public void menu() {

		System.out.println("\n\nMENU DE FUCIONÁRIOS");
		System.out.println("[1] - Registrar funcionário");
		System.out.println("[2] - Listar funcionários");
		System.out.println("[3] - Buscar funcionário");
		System.out.println("[4] - Gerar Relatório");
		System.out.println("[5] - Atualizar Funcionário");
		System.out.println("[6] - Remover Funcionário\n");

		System.out.println("Digite a opção desejada");

		int op = sc.nextInt();
		sc.nextLine();

		switch (op) {
		case 1:
			System.out.println("Informe o nome do funcionário:");
			String nomeFuncionario = sc.nextLine();
			System.out.println("Matrícula de " + nomeFuncionario + ": ");
			String matricula = sc.nextLine();

			System.out.println("[1] - Cadastrar Gerente");
			System.out.println("[2] - Cadastrar Operador de Máquina");
			int opCargo = sc.nextInt();
			sc.nextLine();

			switch (opCargo) {
			case 1:
				System.out.println("Setor:");
				String areaResponsavel = sc.nextLine();
				employeeService.adicionarGerente(nomeFuncionario, matricula, areaResponsavel);
				System.out.println("Gerente cadastro com sucesso!");
				break;
			case 2:
				List<Machine> maquinas = machineService.listarTodasMaquinas();
				if (maquinas.isEmpty()) {
					System.out.println(
							"O Operador de máquina não pode ser cadastrado sem a existência de máquinas no sistema.");
					break;
				}

				for (Machine maquina : maquinas) {
					System.out.println(maquina);
				}

				System.out.println("Atribua o código da máquina que será operada por " + nomeFuncionario + ":");
				int maquinaFuncionarioId = sc.nextInt();
				sc.nextLine();

				employeeService.adicionarOperadorDeMaquina(nomeFuncionario, matricula, maquinaFuncionarioId);
				System.out.println("Operador de máquina cadastrado com sucesso!");
				break;
			default:
				System.out.println("Opção de cargo inválida.");
				break;
			}
			break;
		case 2:
			List<Employee> funcionarios = employeeService.listarTodosFuncionarios();
			if (funcionarios.isEmpty()) {
				System.out.println("Nenhum funcionário cadastrado no sistema.");
			} else {
				for (Employee funcionario : funcionarios) {
					System.out.println(funcionario);
				}
			}
			break;
		case 3:
			if (employeeService.listarTodosFuncionarios().isEmpty()) {
				System.out.println("Nenhum funcionário cadastrado no sistema.");
				break;
			}
			System.out.println("Informe o código do funcionário:");
			int codigoFuncionario = sc.nextInt();
			sc.nextLine();
			Employee funcionario = employeeService.buscarPorId(codigoFuncionario);
			System.out.println(funcionario);
			break;

		case 4:
			System.out.println(employeeService.gerarRelatorio());
			break;

		case 5:
			if (employeeService.listarTodosFuncionarios().isEmpty()) {
				System.out.println("Nenhum funcionário cadastrado no sistema.");
				break;
			}
			System.out.print("Digite o ID do funcionário que deseja atualizar no sistema:");
			int funcionarioId = sc.nextInt();
			sc.nextLine();
			Employee funcionarioBuscar = employeeService.buscarPorId(funcionarioId);

			System.out.println("Informe o novo nome para o funcionário: ");
			String funcionarioAtt = sc.nextLine();

			if (funcionarioBuscar instanceof Manager) {
				System.out.println("Informe o setor responsável: ");
				String setorAtt = sc.nextLine();

				employeeService.atualizarGerente(funcionarioId, funcionarioAtt, setorAtt);
				System.out.println("Informações do funcionário atualizadas");
			} else {
				System.out.println("Informe o ID da nova máquina que será atribuída para " + funcionarioAtt);
				List<Machine> maquinas = machineService.listarTodasMaquinas();
				if (maquinas.isEmpty()) {
					System.out.println(
							"O Operador de máquina não pode ser atualizado sem a existência de máquinas no sistema.");
					break;
				}
				for (Machine maquina : maquinas) {
					System.out.println(maquina);
				}
				int maquinaId = sc.nextInt();
				sc.nextLine();

				employeeService.atualizarOperadorDeMaquina(funcionarioId, maquinaId, funcionarioAtt);
				System.out.println("Informações do funcionário atualizadas");

			}
			break;

		case 6:
			if (employeeService.listarTodosFuncionarios().isEmpty()) {
				System.out.println("Nenhum funcionário cadastrado no sistema.");
				break;
			}
			System.out.print("Digite o ID do funcionário que deseja remover do sistema:");
			int funcionarioExcluirId = sc.nextInt();
			sc.nextLine();
			System.out.println(
					"Tem certeza que deseja remover o funcionário do ID " + funcionarioExcluirId + " do sistema?[S/N]");
			char resp = sc.nextLine().toUpperCase().charAt(0);

			if (resp == 'S') {
				employeeService.remover(funcionarioExcluirId);
				System.out.println("O funcionário do ID: " + funcionarioExcluirId + " foi removido do sistema.");

			} else if (resp == 'N') {
				System.out.println("Operação cancelada pelo usuário.");

			} else {
				System.out.println("Opção inválida!");
			}

			break;
		default:
			System.out.println("Opção inválida.");
			break;

		}
	}
}
