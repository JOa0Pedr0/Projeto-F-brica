package application;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

import menu.EmployeeMenu;
import menu.MachineMenu;
import menu.MainMenu;
import menu.ProductMenu;
import service.EmployeeService;
import service.MachineService;
import service.ProductService;
import service.exceptions.ResourceNotFoundException;

public class Program {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		ProductService productService = new ProductService();
		MachineService machineService = new MachineService();
		EmployeeService employeeService = new EmployeeService();

		ProductMenu productMenu = new ProductMenu(productService, machineService, sc);
		MachineMenu machineMenu = new MachineMenu(machineService, sc);
		EmployeeMenu employeeMenu = new EmployeeMenu(employeeService, machineService, sc);

		MainMenu mainMenu = new MainMenu(productMenu, machineMenu, employeeMenu, sc);

		try {
			mainMenu.mainMenu();
		} catch (InputMismatchException e) {
			System.out.println("\nPor favor digite um valor válido.");
			sc.next();
		} catch (ResourceNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

}
