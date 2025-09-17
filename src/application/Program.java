package application;

import java.util.Locale;
import java.util.Scanner;

import entities.Product;
import service.ProductService;

public class Program {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);

		ProductService produtoService = new ProductService();

		Scanner sc = new Scanner(System.in);
		int op = -1;
		System.out.println("MENU PRINCIPAL");
		while (op != 0) {
			System.out.println("[1] - Adicionar produto");
			System.out.println("[2] - Listar todos oprodutos");
			System.out.println("[3] - Buscar produto");
			System.out.println("[0] - Sair");

			System.out.println("Digite a opção:");
			op = sc.nextInt();
			switch (op) {
			case 1:
				
				System.out.println("Informe o nome do produto:");
				sc.nextLine();
				String nome = sc.nextLine();

				System.out.println("Informe valor de custo do produto:");
				double precoCusto = sc.nextDouble();

				System.out.println("Informe uma breve descrição do produto:");
				sc.nextLine();
				String descricao = sc.nextLine();

				produtoService.AdicionarProduto(new Product(precoCusto, nome, descricao));
				break;

			case 2:

				produtoService.listarTodosProdutos();
				break;

			case 3:
				System.out.println("Informe o código do produto:");
				int codigoProduto = sc.nextInt();
				Product produtoBuscado = produtoService.buscarPorId(codigoProduto);
				if(produtoBuscado == null) {
					System.out.println("Produto não encontrado.");
				}else {
					System.out.println(produtoBuscado);
				}
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
