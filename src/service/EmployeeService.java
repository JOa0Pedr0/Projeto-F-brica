package service;

import java.util.ArrayList;
import java.util.List;

import entities.Employee;

public class EmployeeService {
	private List<Employee> employees = new ArrayList();
	
	public void adicionarFuncionario(Employee funcionario) {
		if(funcionario != null) {
			employees.add(funcionario);
			System.out.println("Funcionário cadastrado com sucesso.");
		}
	}
	
	public void listarTodosFuncionarios() {
		if(employees.isEmpty()) {
			System.out.println("A lista de funcionários se encontra vazia.");
		}else {
			for(Employee employee : employees) {
				System.out.println(employee);
			}
		}	
	}
	
	public Employee buscarPorId(int id) {
		for(Employee employee : employees) {
			if(employee.getId() == id) {
				return employee;
			}
		}
		return null;
	}
}
