package menu;

import java.util.Scanner;

public class MainMenu {

	private ProductMenu productMenu;
	private MachineMenu machineMenu;
	private EmployeeMenu employeeMenu;
	private Scanner sc;

	public MainMenu(ProductMenu productMenu, MachineMenu machineMenu, EmployeeMenu employeeMenu, Scanner sc) {
		this.employeeMenu = employeeMenu;
		this.machineMenu = machineMenu;
		this.productMenu = productMenu;
		this.sc = sc;
	}

	public void mainMenu() {
		int op = -1;
		while (op != 0) {
			System.out.println("\n----Menu Principal----");
			System.out.println("[1] - Menu Produto ");
			System.out.println("[2] - Menu Funcionário");
			System.out.println("[3] - Menu Máquina");
			System.out.println("[0] - Encerrar Programa\n");

			System.out.print("Digite a opção desejada: ");
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
				;

				break;
			case 0:
				System.out.println("saindo...");
				break;
			default:
				sc.nextLine();
				System.out.println("Opção inválida");
				break;
			}
		}

	}
}
