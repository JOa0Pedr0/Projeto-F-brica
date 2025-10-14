package application;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

import menu.EmployeeMenu;
import menu.MachineMenu;
import menu.MainMenu;
import menu.ProductMenu;
import menu.ProductionOrderMenu;
import service.EmployeeService;
import service.MachineService;
import service.ProductService;
import service.ProductionOrderService;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);

		MachineService machineService = new MachineService();
		EmployeeService employeeService = new EmployeeService();
		ProductionOrderService productionOrderService = new ProductionOrderService();
		ProductService productService = new ProductService();

		Scanner scanner = new Scanner(System.in);
		MachineMenu machineMenu = new MachineMenu(machineService, scanner);
		EmployeeMenu employeeMenu = new EmployeeMenu(employeeService, machineService, scanner);
		ProductMenu productMenu = new ProductMenu(productService, machineService, scanner, productionOrderService,
				employeeService);
		ProductionOrderMenu productionOrderMenu = new ProductionOrderMenu(productionOrderService, scanner);

		MainMenu mainMenu = new MainMenu(productMenu, machineMenu, employeeMenu, scanner, productionOrderMenu);

		int op = -1;

		mainMenu.mainMenu();

	}

}