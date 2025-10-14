package menu;

import java.util.InputMismatchException;
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

		System.out.println("\n\nMENU DE MÁQUINAS");
		System.out.println("[1] - Adicionar máquina");
		System.out.println("[2] - Listar todas máquinas");
		System.out.println("[3] - Buscar Máquina");
		System.out.println("[4] - Gerar Relatório");
		System.out.println("[5] - Atualizar Máquina");
		System.out.println("[6] - Remover Máquina\n");

		System.out.print("Digita a opção desejada:");

		int op = sc.nextInt();
		sc.nextLine();
		switch (op) {
		case 1:
			System.out.print("\nInforme o modelo da máquina: ");
			String modelo = sc.nextLine();
			System.out.println("\nStatus da máquina:");
			System.out.println("[1] - OPERANDO");
			System.out.println("[2] - PARADA");
			System.out.println("[3] - EM_MANUTENCAO");

			StatusMachine status = escolherStatus();
			machineService.adicionarMaquina(modelo, status);
			System.out.println("Máquina adicionada com sucesso!");
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
			listaDeMaquinas = machineService.listarTodasMaquinas();
			if (listaDeMaquinas.isEmpty()) {
				System.out.println("Não há maquinas registradas no sistema.");
				break;
			}
			System.out.println("Informe o código do produto:");
			int codigoMaquina = sc.nextInt();
			Machine maquinaBuscado = machineService.buscarPorId(codigoMaquina);
			System.out.println(maquinaBuscado);
			break;

		case 4:
			System.out.println(machineService.gerarRelatorio());
			break;
		case 5:
			if (machineService.listarTodasMaquinas().isEmpty()) {
				System.out.println("Não há maquinas registradas no sistema.");
				break;
			}
			System.out.print("Informe o ID da máquina que deseja atualizar: ");
			int maquinaAAtualizarId = sc.nextInt();
			sc.nextLine();
			machineService.buscarPorId(maquinaAAtualizarId);
			System.out.println("Digite o modelo da máquina: ");
			String modeloAtualizado = sc.nextLine();
			

			StatusMachine statusAtualizado = escolherStatus();

			machineService.atualizarMaquina(maquinaAAtualizarId, modeloAtualizado, statusAtualizado);
			System.out.println("Máquina atualizada com sucesso!");
			break;

		case 6:
			if (machineService.listarTodasMaquinas().isEmpty()) {
				System.out.println("Não há maquinas registradas no sistema.");
				break;
			}
			System.out.print("Informe o ID da máquina que deseja remover: ");

			int maquinaARemoverId = sc.nextInt();
			sc.nextLine();
			System.out.println(
					"Tem certeza que deseja remover a máquina do ID " + maquinaARemoverId + " do sistema?[S/N]");
			char resp = sc.nextLine().toUpperCase().charAt(0);

			if (resp == 'S') {
				machineService.removerMaquina(maquinaARemoverId);
				System.out.println("A máquina do ID: " + maquinaARemoverId + " foi removida do sistema.");

			} else if (resp == 'N') {
				System.out.println("Operação cancelada pelo usuário.");

			} else {
				System.out.println("Opção inválida!");
			}

			break;

		default:
			System.out.println("Opção indisponível.");
			break;
		}

	}

	private StatusMachine escolherStatus() {
		while (true) {
			System.out.println("\nEscolha o status da máquina:");
			System.out.println("[1] - OPERANDO");
			System.out.println("[2] - PARADA");
			System.out.println("[3] - EM_MANUTENCAO");
			System.out.print("Opção de status: ");

			try {
				int statusInt = sc.nextInt();
				sc.nextLine();
				switch (statusInt) {
				case 1:
					return StatusMachine.OPERANDO;
				case 2:
					return StatusMachine.PARADA;
				case 3:
					return StatusMachine.EM_MANUTENCAO;
				default:
					System.out.println("ERRO: Opção de status inválida. Tente novamente.");
				}
			} catch (InputMismatchException e) {
				System.out.println("ERRO: Por favor, digite apenas números.");
				sc.nextLine();
			}
		}
	}

}
