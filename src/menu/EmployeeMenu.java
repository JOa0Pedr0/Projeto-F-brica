package menu;

import java.util.List;
import java.util.Scanner;

import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
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

		sc = new Scanner(System.in);
		System.out.println("\n\nMENU DE FUNCIONÁRIOS");
		System.out.println("[1] - Registrar funcionário");
		System.out.println("[2] - Listar funcionários");
		System.out.println("[3] - Buscar funcionário");
		System.out.println("[4] - Gerar Relatório\n");

		System.out.println("Digite a opção desejada");

		int op = sc.nextInt();

		switch (op) {
		case 1:
			sc.nextLine();
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
				employeeService.adicionarFuncionario(new Manager(nomeFuncionario, matricula, areaResponsavel));
				break;
			case 2:

				machineService.listarTodasMaquinas();

				System.out.println("Atribua o código da máquina que será operada por " + nomeFuncionario + ":");
				int maquinaFuncionario = sc.nextInt();
				Machine maquina = machineService.buscarPorId(maquinaFuncionario);

				employeeService.adicionarFuncionario(new OperatorMachine(nomeFuncionario, matricula, maquina));
				break;
			default:
				System.out.println("Opção de cargo inválida.");
				break;
			}
			break;
		case 2:

			if (employeeService.listarTodosFuncionarios().isEmpty()) {
				System.out.println("Nenhum funcionário cadastrado no sistema.");
			} else {
				List<Employee> employees = employeeService.listarTodosFuncionarios();
				for (Employee emp : employees) {
					System.out.println(emp);
				}
			}
			break;
		case 3:
			System.out.println("Informe o código do funcionário:");
			int codigoFuncionario = sc.nextInt();
			Employee funcionario = employeeService.buscarPorId(codigoFuncionario);
			System.out.println(funcionario);
			break;

		case 4:
			System.out.println(employeeService.gerarRelatorio());
			break;
		default:
			System.out.println("Opção inválida.");
			break;

		}
	}
}
