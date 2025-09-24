package service;

import java.util.ArrayList;
import java.util.List;

import entities.Machine;
import service.exceptions.ResourceNotFoundException;

public class MachineService {

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

}
