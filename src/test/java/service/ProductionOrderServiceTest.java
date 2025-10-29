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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.EmployeeDAO;
import dao.MachineDAO;
import dao.ProductDAO;
import dao.ProductionOrderDAO;
import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import entities.OrderStatus;
import entities.Product;
import entities.ProductionOrder;
import entities.StatusMachine;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductionOrderServiceTest {

	@Mock
	private MachineDAO machineDAOMock;
	@Mock
	private ProductionOrderDAO productionOrderDAOMock;
	@Mock
	private EmployeeDAO employeeDAOMock;
	@Mock
	private ProductDAO productDAOMock;
	@InjectMocks
	private ProductionOrderService productionOrderService;

	@Test
	@DisplayName("Deve criar uma Ordem de Produção com sucesso quando os dados forem válidos.")
	public void deveCriarOrdemDeProducaoComSucesso() {
		int produtoId = 1;
		int quantidade = 10;
		int maquinaId = 2;
		int funcionarioId = 3;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		Employee OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaExistente);
		when(productDAOMock.buscarPorId(produtoId)).thenReturn(produtoExistente);
		when(employeeDAOMock.buscarPorId(funcionarioId)).thenReturn(OperadorMaquinaExistente);

		productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaId, funcionarioId);
	}

	@Test
	@DisplayName("Deve Iniciar Ordem quando a Ordem de Produção já está criada válida.")
	public void deveIniciarOrdemDeProducaoQuandoJaExistirUmaOrdemCriadaValida() {

		int ordemIniciarId = 1;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		OperatorMachine OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		ProductionOrder ordemDeProducaoCriada = new ProductionOrder(produtoExistente, 2, maquinaExistente,
				OperadorMaquinaExistente);

		when(productionOrderDAOMock.buscarPorId(ordemIniciarId)).thenReturn(ordemDeProducaoCriada);
		productionOrderService.iniciarOrdem(ordemIniciarId);
		verify(productionOrderDAOMock, times(1)).atualizar(ordemDeProducaoCriada);

	}

	@Test
	@DisplayName("Deve concluir Ordem de Produção quando a mesma estiver com seu Staus igual a EM_ANDAMENTO.")
	public void deveConcluirOrdemQuandoEstiverComStatusEmAndamento() {
		int ordemConcluirId = 2;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		OperatorMachine OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		ProductionOrder ordemDeProducaoIniciada = new ProductionOrder(produtoExistente, 2, maquinaExistente,
				OperadorMaquinaExistente);

		ordemDeProducaoIniciada.setStatus(OrderStatus.EM_ANDAMENTO);

		when(productionOrderDAOMock.buscarPorId(ordemConcluirId)).thenReturn(ordemDeProducaoIniciada);
		productionOrderService.concluirOrdem(ordemConcluirId);
		verify(productionOrderDAOMock, times(1)).atualizar(ordemDeProducaoIniciada);
	}

	@Test
	@DisplayName("Deve cancelar Ordem de Produção quando a mesma estiver com status PENDENTE.")
	public void deveCancelarOrdemDeProducaoComStatusPendente() {
		int ordemPendenteId = 22;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		OperatorMachine OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		ProductionOrder ordemDeProducaoPendente = new ProductionOrder(produtoExistente, 2, maquinaExistente,
				OperadorMaquinaExistente);
		when(productionOrderDAOMock.buscarPorId(ordemPendenteId)).thenReturn(ordemDeProducaoPendente);
		productionOrderService.cancelarOrdem(ordemPendenteId);
		verify(productionOrderDAOMock, times(1)).atualizar(ordemDeProducaoPendente);

	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com quantidade menor ou igual a 0.")
	public void deveLancarExcecaoAoTentarCriarOrdemComQuantidadeNegativaOuIgualAZero() {
		int produtoId = 1;
		int quantidade = -10;
		int maquinaId = 2;
		int funcionarioId = 3;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		Employee OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaExistente);
		when(productDAOMock.buscarPorId(produtoId)).thenReturn(produtoExistente);
		when(employeeDAOMock.buscarPorId(funcionarioId)).thenReturn(OperadorMaquinaExistente);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaId, funcionarioId);
		});

		String expectedMessage = "A quantidade para uma ordem de produção deve ter um valor acima de 0.";

		assertEquals(expectedMessage, exception.getMessage());

	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com uma instância de Manager.")
	public void deveLancarExcecaoAoTentarCriarOrdemComGerente() {

		int produtoId = 1;
		int quantidade = 10;
		int maquinaId = 2;
		int gerenteId = 3;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		Employee OperadorMaquinaExistente = new Manager("Gerente", "G-001", "Setor teste");

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaExistente);
		when(productDAOMock.buscarPorId(produtoId)).thenReturn(produtoExistente);
		when(employeeDAOMock.buscarPorId(gerenteId)).thenReturn(OperadorMaquinaExistente);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaId, gerenteId);
		});

		assertTrue(exception.getMessage()
				.contains("Não é possível vincular um Gerente a uma ordem de produção, A operação "
						+ "deve ser realizada por um Operador de Máquina."));

		verify(productionOrderDAOMock, never()).cadastrar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com StatusMachine diferente de OPERANDO.")
	public void deveLancarExcecaoAoTentarCriarOrdemDeProducaoComStatusMachineInvalido() {
		int produtoId = 1;
		int quantidade = 10;
		int maquinaId = 2;
		int operadorId = 3;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.EM_MANUTENCAO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		Employee OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		when(machineDAOMock.buscarPorId(maquinaId)).thenReturn(maquinaExistente);
		when(productDAOMock.buscarPorId(produtoId)).thenReturn(produtoExistente);
		when(employeeDAOMock.buscarPorId(operadorId)).thenReturn(OperadorMaquinaExistente);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaId, operadorId);
		});

		assertTrue(exception.getMessage()
				.contains("A máquina não se encontra disponível para criar uma nova ordem de produção."));

		verify(productionOrderDAOMock, never()).cadastrar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com ID do produto inexistente.")
	public void deveLancarExcecaoAoTentarCriarOrdemDeProducaoComIdProdutoInvalido() {
		int produtoIdInvalido = 99;
		int quantidade = 10;
		int maquinaId = 2;
		int operadorId = 3;

		when(productDAOMock.buscarPorId(produtoIdInvalido))
				.thenThrow(new ResourceNotFoundException("Produto não encontrado ID " + produtoIdInvalido));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoIdInvalido, quantidade, maquinaId, operadorId);
		});

		assertTrue(exception.getMessage().contains("Produto não encontrado ID " + produtoIdInvalido));
		verify(productionOrderDAOMock, never()).cadastrar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com ID da máquina inexistente.")
	public void deveLancarExcecaoAoTentarCriarOrdemDeProducaoComIdMaquinaInvalido() {

		int produtoId = 99;
		int quantidade = 10;
		int maquinaIdInvalido = 2;
		int operadorId = 3;

		when(machineDAOMock.buscarPorId(maquinaIdInvalido))
				.thenThrow(new ResourceNotFoundException("Máquina não encontrada para o ID " + maquinaIdInvalido));
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaIdInvalido, operadorId);
		});

		assertTrue(exception.getMessage().contains("Máquina não encontrada para o ID " + maquinaIdInvalido));
		verify(productionOrderDAOMock, never()).cadastrar(any(ProductionOrder.class));

	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar criar Ordem de Produção com ID do funcionário inexistente.")
	public void deveLancarExcecaoAoTentarCriarOrdemDeProducaoComIdFuncionarioInvalido() {

		int produtoId = 99;
		int quantidade = 10;
		int maquinaId = 2;
		int operadorIdInvalido = 3;

		when(employeeDAOMock.buscarPorId(operadorIdInvalido))
				.thenThrow(new ResourceNotFoundException("Funcionário não encontrado ID: " + operadorIdInvalido));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			productionOrderService.criarNovaOrdem(produtoId, quantidade, maquinaId, operadorIdInvalido);
		});

		assertTrue(exception.getMessage().contains("Funcionário não encontrado ID: " + operadorIdInvalido));
		verify(productionOrderDAOMock, never()).cadastrar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar iniciar uma Ordem de Produção com ID inválido.")
	public void deveLancarExcecaoAoTentarIniciarOrdemDeProducaoComIdInvalido() {

		int ordemProducaoIdInvalido = 99;

		when(productionOrderDAOMock.buscarPorId(ordemProducaoIdInvalido)).thenThrow(
				new ResourceNotFoundException("Ordem de produção não encontrada ID: " + ordemProducaoIdInvalido));
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			productionOrderService.iniciarOrdem(ordemProducaoIdInvalido);
		});

		assertTrue(exception.getMessage().contains("Ordem de produção não encontrada ID: " + ordemProducaoIdInvalido));

		verify(productionOrderDAOMock, never()).atualizar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar iniciar uma Ordem de Produção com Status diferente de PENDETE")
	public void deveLancarExcecaoAoTentarIniciarOrdemDeProducaoComStatusInvalido() {
		int ordemProducao = 2;

		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		OperatorMachine OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		ProductionOrder ordemStatusInvalido = new ProductionOrder(produtoExistente, 10, maquinaExistente,
				OperadorMaquinaExistente);
		ordemStatusInvalido.setStatus(OrderStatus.CONCLUIDA);

		when(productionOrderDAOMock.buscarPorId(ordemProducao)).thenReturn(ordemStatusInvalido);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productionOrderService.iniciarOrdem(ordemProducao);
		});

		assertTrue(exception.getMessage().contains("Só é possível iniciar uma ordem que está pendente."));
		verify(productionOrderDAOMock, never()).atualizar(any(ProductionOrder.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar cancelar Ordem de Produção com ID inválido.")
	public void deveLancarExcecaoAoTentarCancelarOrdemDeProducaoComIdInvalido() {
		int ordemProducao = 10;

		when(productionOrderDAOMock.buscarPorId(ordemProducao))
				.thenThrow(new ResourceNotFoundException("Ordem de produção não encontrada ID: " + ordemProducao));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			productionOrderService.cancelarOrdem(ordemProducao);
		});
		
		assertTrue(exception.getMessage().contains("Ordem de produção não encontrada ID: " + ordemProducao));
		verify(productionOrderDAOMock, never()).atualizar(any(ProductionOrder.class));
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao tentar cancelar Ordem de Produção com Status inválido")
	public void deveLancarExcecaoAoTentarCancelarOrdemDeProducaoComStatusInvalido() {
		
		int ordemProducao = 1;
		
		Machine maquinaExistente = new Machine("Máquina teste", StatusMachine.OPERANDO);
		Product produtoExistente = new Product(20.99, "Produto test", "test", maquinaExistente);
		OperatorMachine OperadorMaquinaExistente = new OperatorMachine("Operador teste", "OP-001", maquinaExistente);

		ProductionOrder ordemStatusInvalido = new ProductionOrder(produtoExistente, 10, maquinaExistente,
				OperadorMaquinaExistente);
		ordemStatusInvalido.setStatus(OrderStatus.EM_ANDAMENTO);
		
		when(productionOrderDAOMock.buscarPorId(ordemProducao)).thenReturn(ordemStatusInvalido);
		
		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			productionOrderService.cancelarOrdem(ordemProducao);
		});
		
		assertTrue(exception.getMessage().contains("Só é possível cancelar uma ordem que esteja pendente."));
		verify(productionOrderDAOMock, never()).atualizar(any(ProductionOrder.class));

	}

}
