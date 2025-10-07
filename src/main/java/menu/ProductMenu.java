package menu;

import java.util.List;
import java.util.Scanner;

import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import entities.Product;
import entities.StatusMachine;
import service.EmployeeService;
import service.MachineService;
import service.ProductService;
import service.ProductionOrderService;
import service.exceptions.BusinessRuleException;

public class ProductMenu {

	private ProductService productService;
	private MachineService machineService;
	private Scanner sc;
	private ProductionOrderService orderProduction;
	private EmployeeService employeeService;

	public ProductMenu(ProductService productService, MachineService machineService, Scanner sc,
			ProductionOrderService orderProduction, EmployeeService employeeService) {

		this.productService = productService;
		this.machineService = machineService;
		this.sc = sc;
		this.orderProduction = orderProduction;
		this.employeeService = employeeService;

	}

	public void menu() {

		System.out.println("\n\nMENU DE PRODUTOS");

		System.out.println("[1] - Adicionar Produto");
		System.out.println("[2] - Listar Produtos");
		System.out.println("[3] - Buscar Produto");
		System.out.println("[4] - Criar Ordem de Produção");
		System.out.println("[5] - Gerar Relatório\n");

		System.out.print("Digite a opção desejada:");
		int op = sc.nextInt();

		switch (op) {
		case 1:
			List<Machine> maquinas = machineService.listarTodasMaquinas();
			if (maquinas.isEmpty()) {
				System.out.println("Nenhuma máquina cadastrada no momento.");
				break;
			}

			for (Machine maquina : maquinas) {
				System.out.println(maquina);
			}

			System.out.print("Informe o código da máquina associada ao produto:");
			int codigoMaquina = sc.nextInt();
			Machine machine = machineService.buscarPorId(codigoMaquina);
			sc.nextLine();
			System.out.print("Informe o nome do produto: ");
			String nomeProduto = sc.nextLine();

			System.out.print("Informe valor de custo do produto: ");
			double precoCusto = sc.nextDouble();

			System.out.print("Informe uma breve descrição do produto: ");
			sc.nextLine();
			String descricao = sc.nextLine();

			productService.adicionarProduto(new Product(precoCusto, nomeProduto, descricao, machine));
			break;
		case 2:
			List<Product> listaDeProdutos = productService.listarTodosProdutos();
			if (listaDeProdutos.isEmpty()) {
				System.out.println("Não há produtos cadastrados no sistema.");
			} else {
				for (Product produto : listaDeProdutos) {
					System.out.println(produto);
				}
			}
			break;

		case 3:
			if (productService.listarTodosProdutos().isEmpty()) {
				System.out.println("Ainda não há produtos cadastrados.");
				break;
			}
			System.out.println("Informe o código do produto:");
			int codigoProduto = sc.nextInt();
			Product produtoBuscado = productService.buscarPorId(codigoProduto);
			System.out.println(produtoBuscado);
			break;
		case 4:
			if (machineService.listarTodasMaquinas().isEmpty()) {
				System.out.println("Não há máquinas cadastradas para  realizar essa operação.");
				break;
			}
			if (productService.listarTodosProdutos().isEmpty()) {
				System.out.println("Ainda não há produtos registrados para essa operação.");
				break;
			}
			if (employeeService.listarTodosFuncionarios().isEmpty()) {
				System.out.println("Ainda não há operadores para atribuir em uma ordem de produção.");
				break;
			}
			System.out.println("Digite o ID produto que será enviado para ordem de produção: ");
			int idProdutoOrdem = sc.nextInt();
			Product produtoAtribuido = productService.buscarPorId(idProdutoOrdem);
			if (produtoAtribuido == null) {
				System.out.println("Produto não encontrado.");
				break;
			}

			System.out.print("Informe a quantidade a ser produzida:");
			int quantidade = sc.nextInt();
			if (quantidade < 1) {
				System.out.println("quantidade mínima inválida.");
				break;
			}
			System.out.println("Escollha uma máquina compatível da lista abaixo: (Informe o ID da máquina desejada.)");
			List<Machine> maquinasOrdem = machineService.listarTodasMaquinas();
			for (Machine maquinaOrdem : maquinasOrdem) {
				System.out.println(maquinaOrdem);
			}

			int maquinaId = sc.nextInt();
			Machine maquinaAtribuida = machineService.buscarPorId(maquinaId);

			if (maquinaAtribuida.getStatus() != StatusMachine.OPERANDO) {

				throw new BusinessRuleException("A máquina '" + maquinaAtribuida.getModelo()
						+ "' não está disponível (Status: " + maquinaAtribuida.getStatus() + ").");
			}

			System.out.println("Atribua um operador para a tarefa: (Digite o ID do operador desejado)");
			List<Employee> operadoresOrdem = employeeService.listarTodosFuncionarios();

			for (Employee operadorOrdem : operadoresOrdem) {
				System.out.println(operadorOrdem.getNome() + " - " + operadorOrdem.getId());
			}

			int idOperador = sc.nextInt();
			Employee funcionario = employeeService.buscarPorId(idOperador);
			if(funcionario instanceof Manager) {
				System.out.println("Não é permitido associar um funcionário com cargo de gerente a uma máquina.");
				break;
			}
			OperatorMachine operadorAtribuido = (OperatorMachine) funcionario;

			orderProduction.criarNovaOrdem(produtoAtribuido, quantidade, maquinaAtribuida, operadorAtribuido);

			break;
		case 5:
			System.out.println(productService.gerarRelatorio());
			break;
		default:
			System.out.println("Opção inválida.");
			break;
		}
	}
}
