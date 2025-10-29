package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dao.MachineDAO;
import entities.Machine;
import entities.StatusMachine;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MachineServiceTest {

	@Mock
	private MachineDAO machineDAOMock;
	@InjectMocks
	private MachineService machineService;

	@Test
	@DisplayName("Deve lançar exceção ao adicionar máquina com STATUS nulo.")
	public void deveLancarExcecaoAoAdicionarMaquinaComStatusNull() {
		String modelo = "Máquina Teste";

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			machineService.adicionarMaquina(modelo, null);
		});

		String expectedMessage = "O status da máquina não foi encontrado. Só é permitido cadastrar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO";
		assertEquals(expectedMessage, exception.getMessage());

	}

	@Test
	@DisplayName("Deve adicionar uma máquina com sucesso quando os dados são válidos.")
	public void deveAdicionarMaquinaComSucesso() {
		String modelo = "Maquina Nova Teste";
		StatusMachine status = StatusMachine.OPERANDO;

		machineService.adicionarMaquina(modelo, status);
	}

	@Test
	@DisplayName("Deve lançar execeção ao atualizar máquina com STATUS nulo.")
	public void deveLancarExcecaoAoAtualizarMaquinaComStatusNulo() {
		int maquinaId = 2;
		String novoModelo = "Atualizada";

		Machine maquinaDesatualizada = new Machine("Desatualizada", StatusMachine.PARADA);

		when(machineService.buscarPorId(maquinaId)).thenReturn(maquinaDesatualizada);

		BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
			machineService.atualizarMaquina(maquinaId, novoModelo, null);
		});

		assertEquals(
				"O status da máquina não foi encontrado. Só é permitido atualizar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO",
				exception.getMessage());

	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar remover máquina inexistente.")
	public void deveLancarExcecaoAoRemoverMaquinaInexistente() {
		int maquinaInexistente = 999;

		when(machineService.buscarPorId(maquinaInexistente))
				.thenThrow(new ResourceNotFoundException("Produto não encontrado para o ID: " + maquinaInexistente));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			machineService.removerMaquina(maquinaInexistente);
		});

		assertEquals("Produto não encontrado para o ID: " + maquinaInexistente, exception.getMessage());
	}

	@Test
	@DisplayName("Deve atualizar uma máquina com sucesso.")
	public void deveAtualzarMaquinaComSucesso() {
		int maquinaId = 2;
		String novoNome = "Máquina Atualizada";
		StatusMachine novoStatus = StatusMachine.OPERANDO;

		Machine maquinaAAtualizar = new Machine("Modelo Antigo", StatusMachine.PARADA);

		when(machineService.buscarPorId(maquinaId)).thenReturn(maquinaAAtualizar);
		machineService.atualizarMaquina(maquinaId, novoNome, novoStatus);

		ArgumentCaptor<Machine> machineCaptor = ArgumentCaptor.forClass(Machine.class);

		verify(machineDAOMock, times(1)).atualizar(machineCaptor.capture());

		Machine maquinaAtualizadaPeloServico = machineCaptor.getValue();
		assertEquals(novoNome, maquinaAtualizadaPeloServico.getModelo());
		assertEquals(novoStatus, maquinaAtualizadaPeloServico.getStatus());

	}

}
