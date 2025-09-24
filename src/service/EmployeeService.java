package service;

import java.util.ArrayList;
import java.util.List;

import entities.Employee;
import service.exceptions.ResourceNotFoundException;

public class EmployeeService {
	private List<Employee> employees = new ArrayList();

	public void adicionarFuncionario(Employee funcionario) {
		if (funcionario == null) {
			throw new IllegalArgumentException("Não é possível registrar um funcionário nulo.");
		}
		employees.add(funcionario);
		System.out.println("Funcionário cadastrado com sucesso.");
	}
	public List<Employee> listarTodosFuncionarios() {
		return employees;
	}

	public Employee buscarPorId(int id) {
		for (Employee employee : employees) {
			if (employee.getId() == id) {
				return employee;
			}
		}
		throw new ResourceNotFoundException("Funcionário não encontrado. Id: " + id);
	}

}
