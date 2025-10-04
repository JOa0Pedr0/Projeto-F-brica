package application;


import java.util.Locale;
import java.util.Scanner;

import entities.ProductionOrder;
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
		Scanner sc = new Scanner(System.in);
		ProductService productService = new ProductService();
		MachineService machineService = new MachineService();
		EmployeeService employeeService = new EmployeeService();
		ProductionOrderService productionOrderService = new ProductionOrderService();

		ProductMenu productMenu = new ProductMenu(productService, machineService, sc, productionOrderService, employeeService);
		MachineMenu machineMenu = new MachineMenu(machineService, sc);
		EmployeeMenu employeeMenu = new EmployeeMenu(employeeService, machineService, sc);
		ProductionOrderMenu productionOrderMenu = new ProductionOrderMenu(productionOrderService, sc);

		MainMenu mainMenu = new MainMenu(productMenu, machineMenu, employeeMenu, sc, productionOrderMenu);

		mainMenu.mainMenu();
		

	}

}
