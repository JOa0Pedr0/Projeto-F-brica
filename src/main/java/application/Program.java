package application;


import java.util.List;

import dao.MachineDAO;
import entities.Machine;
import entities.StatusMachine;

public class Program {

	public static void main(String[] args) {

		/*Locale.setDefault(Locale.US);
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
		*/
		
		
		MachineDAO machineDAO = new MachineDAO();
		
		Machine maquinaNova = new Machine("Prensa  60T", StatusMachine.EM_MANUTENCAO);
		
		/*
		
		System.out.println("Listando máquinas cadastradas");
		
		List<Machine> todasMaquinas = machineDAO.listarTodos();
		
		todasMaquinas.forEach(m ->System.out.println(m));
		
		
		System.out.println("Removendo uma máquina");
		
		Machine maquinaParaRemover = machineDAO.buscarPorId(1);
		if(maquinaParaRemover != null) {
			machineDAO.remover(maquinaParaRemover);
		}else {
			System.out.println("Máquina não encontrada.");
		}
		
		System.out.println("Listar máquinas cadastradas (novamente)");
		todasMaquinas = machineDAO.listarTodos();
		todasMaquinas.forEach(m ->System.out.println(m)); */
		
		while(true) {
			
		}
		

	}

}
