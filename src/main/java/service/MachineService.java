package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import service.exceptions.ResourceNotFoundException;

@Service
public class MachineService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(MachineService.class);

	@Autowired
	private MachineDAO machineDAO;
	@Autowired
	private ProductionOrderDAO productionOrderDAO;

	public Machine adicionarMaquina(Machine maquina) {
		if (maquina.getStatus() == null) {
			logger.warn("Tentativa de adicionar máquina com status NULO. Modelo: {}", maquina.getModelo());
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido cadastrar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}
		logger.debug("Tentando cadastrar nova máquina. Modelo: {}", maquina.getModelo());

		Machine maquinaSalva = machineDAO.save(maquina);
		logger.info("Nova máquina cadastrada com ID: {}", maquina.getId());
		return maquinaSalva;
	}

	public List<Machine> listarTodasMaquinas() {
		logger.debug("Buscando lista de todas as máquinas.");
		return machineDAO.findAll();
	}

	public Machine buscarPorId(int id) {
		logger.debug("Buscando máquina por ID: {}", id);
		return machineDAO.findById(id).orElseThrow(() -> {
			logger.warn("Máquina com ID: {} não encontrado.", id);
			return new ResourceNotFoundException("Máquina não encontrada para o ID: " + id);
		});
	}

	public Machine atualizarMaquina(int id, Machine novosDados) {
		logger.debug("Iniciando atualização da máquina ID: {}", id);
		Machine maquinaParaAtualizar = this.buscarPorId(id);

		if (novosDados.getStatus() == null) {
			logger.warn("tentativa de atualizar máquina ID: {} com status NULO.", id);
			throw new BusinessRuleException(
					"O status da máquina não foi encontrado. Só é permitido atualizar uma máquina em estado: OPERANDO, PARADA ou EM_MANUTENCAO");
		}

		maquinaParaAtualizar.setModelo(novosDados.getModelo());
		maquinaParaAtualizar.setStatus(novosDados.getStatus());
		Machine maquinaAtualizada = machineDAO.save(maquinaParaAtualizar);
		return maquinaAtualizada;
	}

	public void removerMaquina(int id) {
		logger.debug("Iniciando remoção da máquina ID: {}", id);
		Machine maquinaRemover = this.buscarPorId(id);

		Long contagem = productionOrderDAO.countByMaquinaId(id);

		boolean historicoMaquina = contagem > 0;

		if (historicoMaquina) {
			logger.warn("Tentativa de remover máquina ID: {} falhou. A Máquina está vinculada a uma Ordem de Produção.", id);
			throw new BusinessRuleException(
					"Não é possível remover uma máquina que tem histórico em uma ordem de produção.");
		}

		machineDAO.delete(maquinaRemover);
		logger.info("Máquina ID: {} removida com sucesso.", id);
	}

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puros para o relatório.");
		List<Machine> maquinas = machineDAO.findAll();

		if (maquinas.isEmpty()) {
			logger.warn("Não há dados para gerar relatório.");
			throw new BusinessRuleException("Nenhum funcionário registrado para gerar relatório.");
		}

		long contOperando = maquinas.stream().filter(m -> m.getStatus() == StatusMachine.OPERANDO).count();

		long contParada = maquinas.stream().filter(m -> m.getStatus() == StatusMachine.PARADA).count();

		long contManutencao = maquinas.stream().filter(m -> m.getStatus() == StatusMachine.EM_MANUTENCAO).count();

		Map<String, Object> dadosRelatorio = new HashMap<>();

		dadosRelatorio.put("totalMaquinas", maquinas.size());
		dadosRelatorio.put("maquinasOperando", contOperando);
		dadosRelatorio.put("maquinasParada", contParada);
		dadosRelatorio.put("maquinaEmManutencao", contManutencao);

		logger.info("Dados do relatório coletados com sucesso.");

		return dadosRelatorio;
	}

	@Override
	public String gerarRelatorio() {

		try {
			Map<String, Object> dados = obterDadosRelatorio();

			StringBuilder sb = new StringBuilder();

			sb.append("--- Relatório de Máquinas ---");
			sb.append("\nMáquina(s) STATUS OPERANDO:").append(dados.get("maquinasOperando"));
			sb.append("\nMáquina(s) STATUS PARADA:").append(dados.get("maquinasParada"));
			sb.append("\nMáquina(s) STATUS EM_MANUTENCAO").append(dados.get("maquinaEmManutencao"));

			return sb.toString();

		} catch (BusinessRuleException e) {
			logger.warn("Operação falhou: {}", e.getMessage());
			return e.getMessage();
		}

	}

}
