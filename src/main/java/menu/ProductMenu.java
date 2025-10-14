package menu;

import java.util.List;
import java.util.Scanner;

import entities.Employee;
import entities.Machine;
import entities.OperatorMachine;
import entities.Product;
import service.EmployeeService;
import service.MachineService;
import service.ProductService;
import service.ProductionOrderService;

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
		System.out.println("[5] - Gerar Relatório");
		System.out.println("[6] - Atualizar Produto");
		System.out.println("[7] - Remover Produto");

		System.out.print("Digite a opção desejada:");
		int op = sc.nextInt();
		sc.nextLine();
		switch (op) {
		case 1:
			List<Machine> maquinas = machineService.listarTodasMaquinas();
			if (maquinas.isEmpty()) {
				System.out.println("Nenhuma máquina cadastrada no momento para realizar a operação.");
				break;
			}

			for (Machine maquina : maquinas) {
				System.out.println(maquina);
			}

			System.out.print("Informe o código da máquina associada ao produto:");
			int maquinaId = sc.nextInt();
			sc.nextLine();

			System.out.print("Informe o nome do produto: ");
			String nomeProduto = sc.nextLine();

			System.out.print("Informe valor de custo do produto: ");
			double precoCusto = sc.nextDouble();
			sc.nextLine();

			System.out.print("Escreva uma breve descrição do produto: ");
			String descricao = sc.nextLine();

			productService.adicionarProduto(nomeProduto, descricao, precoCusto, maquinaId);
			System.out.println("Produto cadastrado com sucesso!");
			break;
		case 2:
			List<Product> produtos = productService.listarTodosProdutos();
			if (produtos.isEmpty()) {
				System.out.println("Não há produtos cadastrados no sistema.");
			} else {
				for (Product produto : produtos) {
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
			int produtoId = sc.nextInt();
			sc.nextLine();
			Product produtoBuscado = productService.buscarPorId(produtoId);
			System.out.println(produtoBuscado);
			break;
		case 4:
			List<Employee> funcionariosOrdem = employeeService.listarTodosFuncionarios();
			List<Machine> maquinasOrdem = machineService.listarTodasMaquinas();
			List<Product> produtosOrdem = productService.listarTodosProdutos();

			if (maquinasOrdem.isEmpty()) {
				System.out.println("Não há máquinas cadastradas para  realizar essa operação.");
				break;
			}
			if (produtosOrdem.isEmpty()) {
				System.out.println("Ainda não há produtos registrados para essa operação.");
				break;
			}
			if (funcionariosOrdem.isEmpty()) {
				System.out.println("Ainda não há operadores para atribuir em uma ordem de produção.");
				break;
			}
			System.out.println("Digite o ID produto que será enviado para ordem de produção: ");

			for (Product produtoOrdem : produtosOrdem) {
				System.out.println(produtoOrdem);
			}
			int produtoOrdemId = sc.nextInt();

			System.out.print("Informe a quantidade a ser produzida:");
			int quantidade = sc.nextInt();

			System.out.println("Escolha uma máquina compatível da lista abaixo: (Informe o ID da máquina desejada.)");

			for (Machine maquinaOrdem : maquinasOrdem) {
				System.out.println(maquinaOrdem);
			}

			int maquinaOrdemId = sc.nextInt();

			System.out.println("Atribua um operador para a tarefa: (Digite o ID do operador desejado)");

			for (Employee operadorOrdem : funcionariosOrdem) {
				if (operadorOrdem instanceof OperatorMachine) {
					System.out.println(operadorOrdem.getNome() + " - " + operadorOrdem.getId());
				}

			}

			int funcionarioOrdemId = sc.nextInt();

			orderProduction.criarNovaOrdem(produtoOrdemId, quantidade, maquinaOrdemId, funcionarioOrdemId);
			System.out.println("Ordem de produção criada.");

			break;
		case 5:
			System.out.println(productService.gerarRelatorio());
			break;
		case 6: 
			List<Machine> maquinasAtt = machineService.listarTodasMaquinas();
			if(productService.listarTodosProdutos().isEmpty()) {
				System.out.println("Não há produtos no sistema disponíveis para atualizar.");
				break;
			}
			if(maquinasAtt.isEmpty()) {
				System.out.println("Não há máquinas no sistema para realizar esta operação.");
				break;
			}
			
			System.out.print("Informe o ID do produto que deseja atualizar:");
			int produtoAAtualizarId = sc.nextInt();
			sc.nextLine();
			if(productService.buscarPorId(produtoAAtualizarId) == null) {
				break;
			}
			System.out.println("Digite o novo nome para o produto:");
			String novoNomeProduto = sc.nextLine();
			System.out.println("Digite a descrição para o produto:");
			String novaDescricao = sc.nextLine();
			System.out.println("Informe o novo preço do produto:");
			double novoPreco = sc.nextDouble();
			sc.nextLine();
			System.out.println("Informe o ID da máquina para o produto:");
			for(Machine maquina : maquinasAtt) {
				System.out.println(maquina);
			}
			int novaMaquinaId = sc.nextInt();
			productService.atualizar(produtoAAtualizarId, novoNomeProduto, novaDescricao, novoPreco, novaMaquinaId);
			System.out.println("Produto atualizado com sucesso!");
			break;
			
		case 7:
			if(productService.listarTodosProdutos().isEmpty()) {
				System.out.println("Não há produtos cadastrados no sistema.");
				break;
			}
			
			System.out.println("Informe o ID do produto que deseja excluir");
			
			int produtoARemoverId = sc.nextInt();
			sc.nextLine();
			
			System.out.println(
					"Tem certeza que deseja remover o produto do ID " + produtoARemoverId + " do sistema?[S/N]");
			char resp = sc.nextLine().toUpperCase().charAt(0);

			if (resp == 'S') {
				productService.remover(produtoARemoverId);
				System.out.println("O produto do ID: " + produtoARemoverId + " foi removido do sistema.");

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
