package menu;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import entities.OrderStatus;
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
		System.out.println("\n--- Gerenciamento de Ordens de Produção ---");
		System.out.println("[1] - Listar Todas as Ordens");
		System.out.println("[2] - Buscar Ordem por ID");
		System.out.println("[3] - Gerar Relatório de Produção");
		System.out.println("[4] - Iniciar Ordem de Produção");
		System.out.println("[5] - Concluir Ordem de Produção");
		System.out.println("[6] - Cancelar Ordem de Produção");

		System.out.print("Digite a opção desejada: ");
		int op = sc.nextInt();
		sc.nextLine();

		switch (op) {
		case 1: 
			List<ProductionOrder> todasAsOrdens = productionOrderService.listarTodas();
			if (todasAsOrdens.isEmpty()) {
				System.out.println("Nenhuma Ordem de Produção registrada.");
			} else {
				System.out.println("\n--- Todas as Ordens de Produção ---");
				todasAsOrdens.forEach(System.out::println);
			}
			break;
		case 2: 
			if (productionOrderService.listarTodas().isEmpty()) {
				System.out.println("Nenhuma Ordem de Produção para buscar.");
				break;
			}
			System.out.print("Informe o ID da ordem que deseja buscar: ");
			int ordemProdutoId = sc.nextInt();
			sc.nextLine();
			ProductionOrder ordemProduto = productionOrderService.buscarPorId(ordemProdutoId);
			System.out.println("Ordem encontrada: " + ordemProduto);
			break;
		case 3: 
			System.out.println(productionOrderService.gerarRelatorio());
			break;
		case 4: 
			
			boolean existemPendentes = listarOrdensPorStatus(OrderStatus.PENDENTE);
			if (existemPendentes) { 
				System.out.print("Digite o ID da ordem que deseja INICIAR: ");
				int ordemProdIniciar = sc.nextInt();
				sc.nextLine();
				productionOrderService.iniciarOrdem(ordemProdIniciar);
				System.out.println("-> Ordem #" + ordemProdIniciar + " atualizada para EM_ANDAMENTO.");
			}
			break;
		case 5:
			boolean existemEmAndamento = listarOrdensPorStatus(OrderStatus.EM_ANDAMENTO);
			if (existemEmAndamento) {
				System.out.print("Digite o ID da ordem que deseja CONCLUIR: ");
				int ordemProdConcluir = sc.nextInt();
				sc.nextLine();
				productionOrderService.concluirOrdem(ordemProdConcluir);
				System.out.println("-> Ordem #" + ordemProdConcluir + " atualizada para CONCLUIDA.");
			}
			break;
		case 6:
			boolean existemPendentesParaCancelar = listarOrdensPorStatus(OrderStatus.PENDENTE);
			if (existemPendentesParaCancelar) {
				System.out.print("Digite o ID da ordem que deseja CANCELAR: ");
				int ordemProdCancelar = sc.nextInt();
				sc.nextLine();
				productionOrderService.cancelarOrdem(ordemProdCancelar);
				System.out.println("-> Ordem #" + ordemProdCancelar + " atualizada para CANCELADA.");
			}
			break;
		default:
			System.out.println("Opção inválida!");
			break;
		}
	}

	
	private boolean listarOrdensPorStatus(OrderStatus status) {
		
		List<ProductionOrder> todasAsOrdens = productionOrderService.listarTodas();
		
		
		List<ProductionOrder> ordensFiltradas = todasAsOrdens.stream()
				.filter(ordem -> ordem.getStatus() == status)
				.collect(Collectors.toList());

		if (ordensFiltradas.isEmpty()) {
			System.out.println("\nNenhuma ordem com o status '" + status + "' encontrada.");
			return false; 
		}

		System.out.println("\n--- Ordens com Status: " + status + " ---");
		
		ordensFiltradas.forEach(System.out::println); 
		return true; 
	}
}