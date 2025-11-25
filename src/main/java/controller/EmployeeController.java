package controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import entities.Employee;
import entities.Manager;
import entities.OperatorMachine;
import service.EmployeeService;

@RestController
@RequestMapping("/api/funcionarios")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	public List<Employee> listarTodos() {
		return employeeService.listarTodosFuncionarios();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Employee> buscarPorId(@PathVariable int id) {
		Employee funcionario = employeeService.buscarPorId(id);
		return ResponseEntity.ok(funcionario);
	}

	@PostMapping("/gerente")
	@ResponseStatus(HttpStatus.CREATED)
	public Manager adicionarGerente(@RequestBody Manager gerente) {
		employeeService.adicionarGerente(gerente);
		return gerente;
	}

	@PostMapping("/operador")
	@ResponseStatus(HttpStatus.CREATED)
	public OperatorMachine adicionarOperador(@RequestBody OperatorMachine operador) {
		employeeService.adicionarOperadorDeMaquina(operador);
		return operador;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable int id) {
		employeeService.remover(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/gerente/{id}")
	public ResponseEntity<Manager> atualizarGerente(@PathVariable int id, @RequestBody Manager gerenteNovosDados) {
		Manager gerenteAtt = employeeService.atualizarGerente(id, gerenteNovosDados);

		return ResponseEntity.ok(gerenteAtt);
	}

	@PutMapping("/operador/{id}")
	public ResponseEntity<OperatorMachine> atualizarOperador(@PathVariable int id,
			@RequestBody OperatorMachine operadorNovosDados) {
		OperatorMachine operadorAtt = employeeService.atualizarOperadorDeMaquina(id, operadorNovosDados);

		return ResponseEntity.ok(operadorAtt);
	}

	@GetMapping("/relatorio")
	public ResponseEntity<Map<String, Object>> getRelatorio() {
		Map<String, Object> dados = employeeService.obterDadosRelatorio();
		return ResponseEntity.ok(dados);
	}

}
