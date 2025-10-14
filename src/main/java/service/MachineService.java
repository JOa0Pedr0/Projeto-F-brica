package service;

import java.util.List;

import dao.MachineDAO;
import entities.Machine;
import entities.StatusMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

public class MachineService implements Reportable {

	private MachineDAO machineDAO = new MachineDAO();

	public void adicionarMaquina(String modelo, StatusMachine status) {
		if (status == null) {
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido cadastrar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}
		Machine maquinaNova = new Machine(modelo, status);

		machineDAO.cadastrar(maquinaNova);
	}

	public List<Machine> listarTodasMaquinas() {
		return machineDAO.listarTodos();
	}

	public Machine buscarPorId(int id) {
		return machineDAO.buscarPorId(id);
	}

	public void atualizarMaquina(int id, String modelo, StatusMachine status) {

		Machine maquinaAtualizar = machineDAO.buscarPorId(id);

		if (status == null) {
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido atualizar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}

		maquinaAtualizar.setModelo(modelo);
		maquinaAtualizar.setStatus(status);

		machineDAO.atualizar(maquinaAtualizar);
	}

	public void removerMaquina(int id) {
		
		Machine maquinaRemover = machineDAO.buscarPorId(id);
		
		machineDAO.remover(maquinaRemover);
	}
	
	


	@Override
	public String gerarRelatorio() {
		List<Machine> todasAsMaquinas = machineDAO.listarTodos();
		if (todasAsMaquinas.isEmpty()) {
			return "Relatório indisponível enquanto não houver registro de máquinas.";
		}
		int operando = 0;
		int parada = 0;
		int emManut = 0;
		for (Machine maquina : todasAsMaquinas) {
			switch (maquina.getStatus()) {
			case OPERANDO:
				operando++;
				break;
			case EM_MANUTENCAO:
				emManut++;
				break;
			case PARADA:
				parada++;
				break;
			}
		}
		return "Condições das máquinas:\n" + "OPERANDO = " + operando + "\n" + "PARADA = " + parada + "\n"
				+ "EM MANUTENÇÃO = " + emManut;
	}

}
