package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.EmployeeDAO;
import dao.MachineDAO;
import dao.ProductionOrderDAO;
import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import entities.StatusMachine;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

	@Mock
	private MachineDAO machineDAOMock;
	@Mock
	private ProductionOrderDAO productionOrderDAOMock;
	@Mock
	private EmployeeDAO employeeDAOMock;
	@InjectMocks
	private EmployeeService employeeService;

	@Test
	@DisplayName("Deve adicionar gerente com sucesso quando os dados são válidos.")
	public void deveAdicionarGerenteComSucesso() {
		String nome = "Gerente Novo";
		String matricula = "G-001";
		String areaResponsavel = "Área Responsável teste";

		employeeService.adicionarGerente(nome, matricula, areaResponsavel);
	}

	@Test
	@DisplayName("Deve adicionar operador de máquina com sucesso quando os dados são válidos.")
	public void deveAdicionarOperadorDeMaquinaComSucesso() {
		String nome = "Operador Novo";
		String matricula = "OP-001";
		int maquinaId = 2;
		Machine maquinaTest = new Machine("Máquina teste", StatusMachine.PARADA);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaTest);
		employeeService.adicionarOperadorDeMaquina(nome, matricula, maquinaId);
	}

	@Test
	@DisplayName("Deve atualizar gerente com sucesso quando os dados são válidos.")
	public void deveAtualizarGerenteComSucesso() {
		String novoNome = "Nome Atualizado Gerente";
		String novaAreaResponsavel = "Nova Área";
		int gerenteId = 9;

		Employee gerenteDesatualizado = new Manager("Nome Antigo", "NA-001", "Setor Teste");
		when(employeeService.buscarPorId(gerenteId)).thenReturn(gerenteDesatualizado);
		employeeService.atualizarGerente(gerenteId, novoNome, novaAreaResponsavel);

		ArgumentCaptor<Manager> employeeCaptor = ArgumentCaptor.forClass(Manager.class);
		verify(employeeDAOMock, times(1)).atualizar(employeeCaptor.capture());
		Manager funcionarioAtualizadoPeloServico = employeeCaptor.getValue();
		assertEquals(novoNome, funcionarioAtualizadoPeloServico.getNome());
		assertEquals(novaAreaResponsavel, funcionarioAtualizadoPeloServico.getAreaResponsavel());
	}

	@Test
	@DisplayName("Deve atualizar operador de máquina com sucesso quando os dados são válidos.")
	public void deveAtualizarOperadorDeMaquinaComSucesso() {
		String novoNome = "Nome Atualizado Operador";
		int maquinaNovaId = 9;
		int operadorId = 2;

		Machine maquinaAntiga = new Machine("Máquina Antiga", StatusMachine.EM_MANUTENCAO);
		Employee operadorAAtualizar = new OperatorMachine("Nome Desatualizado Operador", "ND-0001", maquinaAntiga);
		Machine maquinaNova = new Machine("Máquina Nova", StatusMachine.OPERANDO);

		when(machineDAOMock.buscarPorId(maquinaNovaId)).thenReturn(maquinaNova);
		when(employeeService.buscarPorId(operadorId)).thenReturn(operadorAAtualizar);

		employeeService.atualizarOperadorDeMaquina(operadorId, maquinaNovaId, novoNome);

		ArgumentCaptor<OperatorMachine> employeeCaptor = ArgumentCaptor.forClass(OperatorMachine.class);
		verify(employeeDAOMock, times(1)).atualizar(employeeCaptor.capture());
		OperatorMachine funcionarioAtualizadoPeloServico = employeeCaptor.getValue();
		assertEquals(novoNome, funcionarioAtualizadoPeloServico.getNome());
		assertEquals(maquinaNova, funcionarioAtualizadoPeloServico.getMaquinaAlocada());

	}

	@Test
	@DisplayName("Deve remover Gerente com sucesso quando o ID existe.")
	public void deveRemoverFuncionarioComSucesso() {
		int gerenteId = 2;

		Employee gerenteExistente = new Manager("Gerente Para Excluir", "M-001", "Setor_Teste");

		when(employeeService.buscarPorId(gerenteId)).thenReturn(gerenteExistente);

		employeeService.remover(gerenteId);

		verify(employeeDAOMock, times(1)).remover(gerenteExistente);

	}

	@Test
	@DisplayName("Deve remover Operador de Máquina com sucesso quando o ID existe e não há ordens associadas")
	public void deveRemoverOperadorDeMaquinaComSucessso() {

		int operadorMaquinaId = 1;

		Machine maquinaFakeParaOperador = new Machine("Prensa Fake", StatusMachine.OPERANDO);

		OperatorMachine operadorMaquinaExistente = new OperatorMachine("Operador Para Excluir", "OP-001",
				maquinaFakeParaOperador);

		when(employeeDAOMock.buscarPorId(operadorMaquinaId)).thenReturn(operadorMaquinaExistente);

		when(productionOrderDAOMock.existeOrdemComOperadorId(operadorMaquinaId)).thenReturn(false);

		employeeService.remover(operadorMaquinaId);

		verify(employeeDAOMock, times(1)).remover(operadorMaquinaExistente);
		verify(productionOrderDAOMock, times(1)).existeOrdemComOperadorId(operadorMaquinaId);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar remover Operador de Máquina com histórico em uma Ordem de Produção.")
	public void deveLancarExcecaoAoTentarRemoverOperadorDeMaquinaComHistoricoEmOrdemDeProducao() {
		int operadorMaquinaId = 3;

		Machine maquinaFakeParaOperador = new Machine("Prensa Fake", StatusMachine.OPERANDO);

		OperatorMachine operadorMaquinaExistente = new OperatorMachine("Operador Para Excluir", "OP-001",
				maquinaFakeParaOperador);

		when(employeeDAOMock.buscarPorId(operadorMaquinaId)).thenReturn(operadorMaquinaExistente);

		when(productionOrderDAOMock.existeOrdemComOperadorId(operadorMaquinaId)).thenReturn(true);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			employeeService.remover(operadorMaquinaId);
		});

		assertTrue(exception.getMessage()
				.contains("Não é possível remover um operador que tenha histórico em uma ordem de produção."));

		verify(employeeDAOMock, never()).remover(any(Employee.class));

	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar atualizar Gerente com ID inexistente.")
	public void deveLancarExcecaoAoTentarAtualizarGerenteIdInexistente() {
		int gerenteId = 102;
		String novoNome = "Novo nome";
		String novoSetor = "Novo setor";

		when(employeeDAOMock.buscarPorId(gerenteId))
				.thenThrow(new ResourceNotFoundException("Funcionário não encontrado ID: " + gerenteId));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			employeeService.atualizarGerente(gerenteId, novoNome, novoSetor);
		});

		assertEquals("Funcionário não encontrado ID: " + gerenteId, exception.getMessage());
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar atualizar Operador de Máquina com ID de máquina inexistente.")
	public void deveLancarExcecaoAoTentarAtualizarOperadorDeMaquinaComIdDeMaquinaInexistente() {
		int operadorMaquinaId = 10;
		int maquinaIdInexistente = 99;
		String novoNome = "Novo nome";

		Machine maquinaAntiga = new Machine("Modelo Antigo", StatusMachine.EM_MANUTENCAO);

		Employee operadorAAtualizar = new OperatorMachine("Nome antigo", "NA-001", maquinaAntiga);

		when(employeeDAOMock.buscarPorId(operadorMaquinaId)).thenReturn(operadorAAtualizar);

		when(machineDAOMock.buscarPorId(maquinaIdInexistente))
				.thenThrow(new ResourceNotFoundException("Máquina não encontrada para o ID " + maquinaIdInexistente));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			employeeService.atualizarOperadorDeMaquina(operadorMaquinaId, maquinaIdInexistente, novoNome);
		});

		assertEquals("Máquina não encontrada para o ID " + maquinaIdInexistente, exception.getMessage());
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar remover funcionário com ID inexistente.")
	public void deveLancarExcecaoAoTentarRemoverFuncionarioComIdInexistente() {
		int funcionarioIdInexistente = 99;

		when(employeeDAOMock.buscarPorId(funcionarioIdInexistente))
				.thenThrow(new ResourceNotFoundException("Funcionário não encontrado ID: " + funcionarioIdInexistente));
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			employeeService.remover(funcionarioIdInexistente);
		});
		
		assertEquals("Funcionário não encontrado ID: " + funcionarioIdInexistente, exception.getMessage());

	}
}
