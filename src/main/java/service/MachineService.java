package service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.MachineDAO;
import dao.ProductionOrderDAO;
import entities.Machine;
import entities.StatusMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;

@Service
public class MachineService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(MachineService.class);

	@Autowired
	private MachineDAO machineDAO;
	
	private ProductionOrderDAO productionOrderDAO;

	public void adicionarMaquina(String modelo, StatusMachine status) {
		if (status == null) {
			logger.warn("Tentativa de adicionar máquina com status NULO. Modelo: {}", modelo);
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido cadastrar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}
		logger.debug("Tentando cadastrar nova máquina. Modelo: {}", modelo);
		Machine maquinaNova = new Machine(modelo, status);

		machineDAO.cadastrar(maquinaNova);
		logger.info("Nova máquina cadastrada com ID: {}", maquinaNova.getId());
	}

	public List<Machine> listarTodasMaquinas() {
		logger.debug("Buscando lista de todas as máquinas.");
		return machineDAO.listarTodos();
	}

	public Machine buscarPorId(int id) {
		logger.debug("Buscando máquina por ID: {}", id);
		return machineDAO.buscarPorId(id);
	}

	public void atualizarMaquina(int id, String modelo, StatusMachine status) {
		logger.debug("Iniciando atualização da máquina ID: {}", id);
		Machine maquinaAtualizar = machineDAO.buscarPorId(id);

		if (status == null) {
			logger.warn("tentativa de atualizar máquina ID: {} com status NULO.", id);
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido atualizar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}

		maquinaAtualizar.setModelo(modelo);
		maquinaAtualizar.setStatus(status);

		machineDAO.atualizar(maquinaAtualizar);
		logger.info("Máquina ID: {} atualizada com sucesso.", id);
	}

	public void removerMaquina(int id) {
		logger.debug("Iniciando remoção da máquina ID: {}", id);
		Machine maquinaRemover = machineDAO.buscarPorId(id);
		boolean historicoMaquina = productionOrderDAO.existeOrdemComMaquinaId(id);

		if (historicoMaquina) {
			logger.warn("Tentativa de remover máquina ID: {} falhou. Máquina está em uso.");
			throw new BusinessRuleException(
					"Não é possível remover uma máquina que tem histórico em uma ordem de produção.");
		}

		machineDAO.remover(maquinaRemover);
		logger.info("Máquina ID: {} removida com sucesso.", id);
	}

	@Override
	public String gerarRelatorio() {
		logger.debug("Iniciando tentativa de gerar relatório.");
		List<Machine> todasAsMaquinas = machineDAO.listarTodos();
		if (todasAsMaquinas.isEmpty()) {
			logger.warn("Tentativa de gerar relatório falhou. Nenhum histórico de Machine no Banco de Dados.");
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
		logger.info("Relatório gerado com sucesso.");
		return "Condições das máquinas:\n" + "OPERANDO = " + operando + "\n" + "PARADA = " + parada + "\n"
				+ "EM MANUTENÇÃO = " + emManut;
	}

}
