package menu;

import java.util.List;
import java.util.Scanner;

import entities.ProductionOrder;
import service.ProductionOrderService;

public class ProductionOrderMenu {
	private ProductionOrderService productionOrderService;
	private Scanner sc;

	public ProductionOrderMenu(ProductionOrderService productionOrderService, Scanner sc) {
		this.productionOrderService = productionOrderService;
		this.sc = sc;
	}

	public void menu() {
		System.out.println("\n\nMENU ORDEM DE PRODUÇÃO");
		System.out.println("[1] - LISTAR ORDENS DE PRODUTOS");
		System.out.println("[2] - BUSCAR ORDEM DE PRODUTO");
		System.out.println("[3] - GERAR RELATÓRIO");

		System.out.println("Digite a opção desejada:");
		int op = sc.nextInt();
		List<ProductionOrder> listaOrdemDeProduto = productionOrderService.listarTodas();
		switch (op) {
		case 1:

			if (listaOrdemDeProduto.isEmpty()) {
				System.out.println("Nenhuma Ordem de Produção registrada.");
				break;
			}
			for (ProductionOrder ordemProduto : listaOrdemDeProduto) {
				System.out.println(ordemProduto);
			}
			break;
		case 2:
			if (listaOrdemDeProduto.isEmpty()) {
				System.out.println("Nenhuma Ordem de Produção registrada.");
				break;
			}
			System.out.print("Informe o ID da ordem de produto que deseja buscar: ");
			int ordemProdutoId = sc.nextInt();
			ProductionOrder ordemProduto = productionOrderService.buscarPorId(ordemProdutoId);
			System.out.println(ordemProduto);
			break;
		case 3:
			System.out.println(productionOrderService.gerarRelatorio());
			break;
		default:
			System.out.println("Opção inválida!");
			break;
		}
	}

}
