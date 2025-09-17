package application;

import java.util.Locale;
import java.util.Scanner;

import entities.Machine;
import entities.Product;
import entities.StatusMachine;
import service.MachineService;
import service.ProductService;

public class Program {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);

		ProductService produtoService = new ProductService();
		MachineService machineService = new MachineService();

		Scanner sc = new Scanner(System.in);
		int op = -1;
		System.out.println("MENU PRINCIPAL");
		while (op != 0) {
			System.out.println("[1] - Adicionar produto");
			System.out.println("[2] - Listar todos oprodutos");
			System.out.println("[3] - Buscar produto");
			System.out.println("[4] - Adicionar máquina");
			System.out.println("[5] - Listar todas máquinas");
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
				String nome = sc.nextLine();

				System.out.println("Informe valor de custo do produto:");
				double precoCusto = sc.nextDouble();

				System.out.println("Informe uma breve descrição do produto:");
				sc.nextLine();
				String descricao = sc.nextLine();

				produtoService.adicionarProduto(new Product(precoCusto, nome, descricao, machine));

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
				Machine maquina = new Machine();
				maquina.setModelo(modelo);
				System.out.println("Status da máquina:");
				System.out.println("[1] - OPERANDO");
				System.out.println("[2] - PARADA");
				System.out.println("[3] - EM_MANUTENCAO");
				int statusInt = sc.nextInt();
				switch (statusInt) {
				case 1:
					maquina.setStatus(StatusMachine.OPERANDO); 
					break;
				case 2:
					maquina.setStatus(StatusMachine.PARADA); 
					break;
				case 3:
					maquina.setStatus(StatusMachine.EM_MANUTENCAO); 
					break;
				default:
					System.out.println("Opção inválida!");
					break;
				}
				if(maquina.getStatus() == null) {
					break;
				}
					machineService.adicionarMaquina(maquina);
				break;
			
			case 5:
				machineService.listarTodasMaquinas();
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
