package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.Machine;
import interfaces.Reportable;
import service.exceptions.ResourceNotFoundException;

public class MachineService implements Reportable {

	private List<Machine> maquinas = new ArrayList();

	public void adicionarMaquina(Machine maquina) {
		if (maquina == null) {
			throw new IllegalArgumentException("Não é possível registrar uma máquina nula.");
		}
		maquinas.add(maquina);
		System.out.println("Maquina cadastrada com sucesso.");
	}

	public List<Machine> listarTodasMaquinas() {
		return maquinas;
	}

	public Machine buscarPorId(int id) {
		for (Machine maquina : maquinas) {
			if (maquina.getId() == id) {
				return maquina;
			}
		}
		throw new ResourceNotFoundException("Máquina não encontrada. Id: " + id);
	}

	@Override
	public String gerarRelatorio() {
		
		if(maquinas.isEmpty()) {
			return "Relatório indisponível enquanto não houver registro de máquinas.";
		}
		int operando = 0;
		int parada = 0;
		int emManut = 0;
		for (Machine maquina : maquinas) {
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
		return "Condições das máquinas:\n" + "OPERANDO = " + operando + "\n" + "PARADA = "
				+ parada + "\n" + "EM MANUTENÇÃO = " + emManut;
	}

}
