package menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

public class MainMenu {

	private ProductMenu productMenu;
	private MachineMenu machineMenu;
	private EmployeeMenu employeeMenu;
	private Scanner sc;
	private ProductionOrderMenu productionOrderMenu;

	public MainMenu(ProductMenu productMenu, MachineMenu machineMenu, EmployeeMenu employeeMenu, Scanner sc,
			ProductionOrderMenu productionOrderMenu) {
		this.employeeMenu = employeeMenu;
		this.machineMenu = machineMenu;
		this.productMenu = productMenu;
		this.sc = sc;
		this.productionOrderMenu = productionOrderMenu;
	}

	public void mainMenu() {
		int op = -1;
		while (op != 0) {
			System.out.println("\n----Menu Principal----");
			System.out.println("[1] - Menu Produto ");
			System.out.println("[2] - Menu Funcionário");
			System.out.println("[3] - Menu Máquina");
			System.out.println("[4] - Menu Ordem de Produção");
			System.out.println("[0] - Encerrar Programa\n");

			System.out.print("Digite a opção desejada: ");
			try {
				System.out.println();
				op = sc.nextInt();
				switch (op) {
				case 1:
					sc.nextLine();
					productMenu.menu();

					break;
				case 2:
					sc.nextLine();
					employeeMenu.menu();

					break;
				case 3:
					sc.nextLine();
					machineMenu.menu();

					break;
				case 4:
					sc.nextLine();
					productionOrderMenu.menu();

					break;
				case 0:
					System.out.println("saindo...");
					break;
				default:
					sc.nextLine();
					System.out.println("Opção inválida");

					break;
				}
			} catch (

			InputMismatchException e) {
				System.out.println("Entrada inválida, por favor digite uma opção númerica válida.");
				sc.nextLine();

			} catch (jakarta.persistence.RollbackException e) {
				System.out.println("\nERRO DE INTEGRIDADE: A operação não pôde ser concluída.");
				System.out.println(
						"Motivo: Este registro (ex: Máquina, Funcionário...) está sendo usado por outra parte do sistema (ex: um Produto ou uma Ordem de Produção) e não pode ser removido.");
			} catch (ResourceNotFoundException e) {
				System.out.println(e.getMessage());
				sc.nextLine();

			} catch (BusinessRuleException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}
}
