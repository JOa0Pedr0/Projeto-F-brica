package service;

import java.util.ArrayList;
import java.util.List;

import entities.Machine;

public class MachineService {

	private List<Machine> maquinas = new ArrayList();

	public void adicionarMaquina(Machine maquina) {
		if (maquina != null) {
			maquinas.add(maquina);
			System.out.println("Maquina cadastrada com sucesso.");
		}
	}

	public void listarTodasMaquinas() {
		if (maquinas.isEmpty()) {
			System.out.println("Não há maquinas cadastradas.");
		} else {
			for (Machine maquina : maquinas) {
				System.out.println(maquina);
			}
		}
	}

	public Machine buscarPorId(int id) {
		for(Machine maquina : maquinas) {
			if(maquina.getId() == id) {
				return maquina;
		}
	}
		return null;
	}
	
	public boolean isEmpty() {
		return maquinas.isEmpty();
	}
}
