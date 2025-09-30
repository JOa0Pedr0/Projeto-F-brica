package menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import entities.Machine;
import entities.Product;
import service.MachineService;
import service.ProductService;

public class ProductMenu {

	private ProductService productService;
	private MachineService machineService;
	private Scanner sc;

	public ProductMenu(ProductService productService, MachineService machineService, Scanner sc) {
		this.productService = productService;
		this.machineService = machineService;
		this.sc = sc;

	}

	public void menu() {
		

		System.out.println("\n\nMENU DE PRODUTOS");

		System.out.println("[1] - Adicionar Produto");
		System.out.println("[2] - Listar Produtos");
		System.out.println("[3] - Buscar Produto");
		System.out.println("[4] - Gerar Relatório\n");

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
			System.out.println(productService.gerarRelatorio());
			break;
		default:
			System.out.println("Opção inválida.");
			break;
		}
	}
}
