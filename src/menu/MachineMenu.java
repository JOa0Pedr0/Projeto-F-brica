package menu;

import java.util.List;
import java.util.Scanner;

import entities.Machine;
import entities.StatusMachine;
import service.MachineService;

public class MachineMenu {
	private MachineService machineService;
	private Scanner sc;
	public MachineMenu(MachineService machineService, Scanner sc) {
		this.machineService = machineService;
		this.sc = sc;
	}

	public void menu() {
		sc = new Scanner(System.in);
		
		System.out.println("\n\nMENU DE MÁQUINAS");
		System.out.println("[1] - Adicionar máquina");
		System.out.println("[2] - Listar todas máquinas");
		System.out.println("[3] - Buscar Máquina");
		System.out.println("[4] - Gerar Relatório\n");

		System.out.print("Digita a opção desejada:");

		int op = sc.nextInt();
		switch (op) {
		case 1:
			sc.nextLine();
			System.out.print("\nInforme o modelo da máquina: ");
			String modelo = sc.nextLine();
			System.out.println("\nStatus da máquina:");
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
		case 2:
			List<Machine> listaDeMaquinas = machineService.listarTodasMaquinas();
			if (listaDeMaquinas.isEmpty()) {
				System.out.println("Não há maquinas registradas no sistema.");
			} else {
				for (Machine maquina : listaDeMaquinas) {
					System.out.println(maquina);
				}
			}
			break;
		case 3:
			System.out.println("Informe o código do produto:");
			int codigoMaquina = sc.nextInt();
			Machine produtoBuscado = machineService.buscarPorId(codigoMaquina);
			System.out.println(produtoBuscado);
			break;
			
		case 4:
			System.out.println(machineService.gerarRelatorio());
			break;

		default:
			System.out.println("Opção indisponível.");
			break;
		}

	}
}
