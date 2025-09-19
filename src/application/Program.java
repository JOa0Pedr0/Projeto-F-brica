package application;

import java.util.Locale;
import java.util.Scanner;

import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import entities.Product;
import entities.StatusMachine;
import service.EmployeeService;
import service.MachineService;
import service.ProductService;

public class Program {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);

		ProductService produtoService = new ProductService();
		MachineService machineService = new MachineService();
		EmployeeService employeeService = new EmployeeService();

		Scanner sc = new Scanner(System.in);
		int op = -1;
		System.out.println("MENU PRINCIPAL");
		while (op != 0) {
			System.out.println("[1] - Adicionar produto");
			System.out.println("[2] - Listar todos produtos");
			System.out.println("[3] - Buscar produto");
			System.out.println("[4] - Adicionar máquina");
			System.out.println("[5] - Listar todas máquinas");
			System.out.println("[6] - Registrar funcionários");
			System.out.println("[7] - Listar funcionários");
			System.out.println("[0] - Sair");

			System.out.println("Digite a opção:");
			op = sc.nextInt();
			switch (op) {
			case 1:

				machineService.listarTodasMaquinas();
				if (machineService.isEmpty()) {
					System.out.println("ERRO: Nenhuma máquina cadastrada. Adicione uma máquina primeiro.");
					break;
				}

				System.out.print("Informe o código da máquina associada ao produto:");
				int codigoMaquina = sc.nextInt();
				Machine machine = machineService.buscarPorId(codigoMaquina);
				sc.nextLine();
				if (machine == null) {
					System.out.println("Código inválido para cadastro de máquina.");
					break;
				}
				System.out.println("Informe o nome do produto:");
				String nomeProduto = sc.nextLine();

				System.out.println("Informe valor de custo do produto:");
				double precoCusto = sc.nextDouble();

				System.out.println("Informe uma breve descrição do produto:");
				sc.nextLine();
				String descricao = sc.nextLine();

				produtoService.adicionarProduto(new Product(precoCusto, nomeProduto, descricao, machine));

				break;

			case 2:
				produtoService.listarTodosProdutos();
				break;

			case 3:
				System.out.println("Informe o código do produto:");
				int codigoProduto = sc.nextInt();
				Product produtoBuscado = produtoService.buscarPorId(codigoProduto);
				if (produtoBuscado == null) {
					System.out.println("Produto não encontrado.");
				} else {
					System.out.println(produtoBuscado);
				}
				break;
			case 4:
				sc.nextLine();
				System.out.println("Informe o modelo da máquina:");
				String modelo = sc.nextLine();
				System.out.println("Status da máquina:");
				System.out.println("[1] - OPERANDO");
				System.out.println("[2] - PARADA");
				System.out.println("[3] - EM_MANUTENCAO");
				int statusInt = sc.nextInt();
				StatusMachine status = null;
				switch (statusInt) {

				case 1:
					status = StatusMachine.OPERANDO;

					break;
				case 2:
					status = StatusMachine.PARADA;
					break;
				case 3:
					status = StatusMachine.EM_MANUTENCAO;
					break;
				default:
					System.out.println("Opção inválida!");
					break;
				}
				if (status == null) {
					break;
				}
				machineService.adicionarMaquina(new Machine(modelo, status));
				break;

			case 5:
				machineService.listarTodasMaquinas();
				break;
			case 6:
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
					if (machineService.isEmpty()) {
						System.out.println("ERRO: Nenhuma máquina cadastrada. Adicione uma máquina primeiro.");
						break;
					}
					System.out.println("Atribua o código da máquina que será operada por: " + nomeFuncionario + ":");
					sc.nextLine();
					int maquinaFuncionario = sc.nextInt();
					Machine maquina = machineService.buscarPorId(maquinaFuncionario);
					if (maquina == null) {
						System.out.println("ID não encontrado.");
						break;
					}
					employeeService.adicionarFuncionario(new OperatorMachine(nomeFuncionario, matricula, maquina));
					break;
				default:
					System.out.println("Opção de cargo inválida.");
					break;
				}

				break;
			case 7:
				employeeService.listarTodosFuncionarios();
				break;
			case 0:
				System.out.println("Saindo...");
				break;

			default:
				System.out.println("Erro...");
				break;

			}

		}
		sc.close();

	}

}
