package service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.EmployeeDAO;
import dao.MachineDAO;
import dao.ProductionOrderDAO;
import entities.Employee;
import entities.Machine;
import entities.Manager;
import entities.OperatorMachine;
import entities.StatusMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

@Service
public class EmployeeService implements Reportable {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private MachineDAO machineDAO;
	@Autowired
	private ProductionOrderDAO productionOrderDAO;

	public Manager adicionarGerente(Manager gerente) {
		logger.debug("Iniciado cadastro de gerente.");

		Manager gerenteSalvo = employeeDAO.save(gerente);
		logger.info("Cadastro de gerente concluído com sucesso.");
		return gerenteSalvo;
	}

	public OperatorMachine adicionarOperadorDeMaquina(OperatorMachine operadorMaquina) {
		logger.debug("Inciando cadastro de Operador de Máquina.");

		if (operadorMaquina.getMaquinaAlocada() == null) {
			logger.warn("Tentativa de adiconar máquina null.");
			throw new BusinessRuleException("Máquina adicionada não existe.");
		}
		if (machineDAO.findAll().isEmpty()) {
			logger.warn("Tentativa de cadastrar operador falhou: Nenhuma máquina registrada.");
			throw new BusinessRuleException(
					"O Operador de máquina não pode ser cadastrado sem a existência de máquinas no sistema.");
		}

		OperatorMachine operadorSalvo = employeeDAO.save(operadorMaquina);
		logger.info("Operador de máquina adicionado com sucesso.");
		return operadorSalvo;

	}

	public List<Employee> listarTodosFuncionarios() {
		logger.debug("Inciado Listagem de funcionários.");
		return employeeDAO.findAll();
	}

	public Employee buscarPorId(int id) {
		logger.debug("Tentativa de busca de funcionário ID: {}.", id);
		return employeeDAO.findById(id).orElseThrow(() -> {
			logger.warn("Máquina com ID: {} não encontrado.", id);
			return new ResourceNotFoundException("Funcionário não encontrado para o ID: " + id);
		});
	}

	public Manager atualizarGerente(int id, Manager gerente) {
		logger.debug("Inciando atualização do funcionário ID: {}", id);
		Employee funcionario = this.buscarPorId(id);

		if (!(funcionario instanceof Manager)) {
			logger.warn("Falha ao tentar atualizar: ID {} não é um Gerente", id);
			throw new BusinessRuleException("O ID " + id + " não pertence a um Gerente.");
		}

		Manager gerentCast = (Manager) funcionario;
		gerentCast.setNome(gerente.getNome());
		gerentCast.setAreaResponsavel(gerente.getAreaResponsavel());
		Manager gerenteAtt = employeeDAO.save(gerentCast);
		logger.info("Gerente ID: {} atualizado com sucesso.", id);

		return gerenteAtt;
	}

	public OperatorMachine atualizarOperadorDeMaquina(int operadorMaquinaId, OperatorMachine operatorMachine) {
		logger.debug("Inciando atualização do funcionário ID: {}", operadorMaquinaId);
		Employee funcionario = this.buscarPorId(operadorMaquinaId);
		if (!(funcionario instanceof OperatorMachine)) {
			logger.warn("Falha ao tentar atualizar: ID {} não é um Operador de Máquina", operadorMaquinaId);
			throw new BusinessRuleException("O ID " + operadorMaquinaId + " não pertence a um Operador de Máquina.");
		}

		int novaMaquinaId = operatorMachine.getMaquinaAlocada().getId();

		Machine novaMaquina = machineDAO.findById(novaMaquinaId).orElseThrow(() -> {
			logger.warn("Máquina com ID: {} não foi encontrada.", novaMaquinaId);
			throw new ResourceNotFoundException("Máquina com ID: " + novaMaquinaId + " não encontrada.");
		});

		OperatorMachine operadorCast = (OperatorMachine) funcionario;

		operadorCast.setNome(operatorMachine.getNome());
		operadorCast.setMaquinaAlocada(novaMaquina);

		OperatorMachine operadorMaquinaAtt = employeeDAO.save(operadorCast);

		logger.info(" Operador de Máquina ID: {} atualizado com suceso", operadorMaquinaId);
		return operadorMaquinaAtt;
	}

	public void remover(int id) {
		logger.debug("Tentativa de remoção de funcionário ID: {}.", id);

		Employee funcionarioRemover = this.buscarPorId(id);

		logger.debug("Validando a instância do funcionário.");

		if (funcionarioRemover instanceof OperatorMachine) {
			logger.debug("Verificando se o funcionário tem registro em alguma Ordem de Produção.");
			Long contagem = productionOrderDAO.countByOperadorId(id);

			boolean operadorHistorico = contagem > 0;

			if (operadorHistorico) {
				logger.warn("Tentativa inválida de remover funcionário vinculado a uma Ordem de Produção.");
				throw new BusinessRuleException(
						"Não é possível remover um operador que tenha histórico em uma ordem de produção.");
			}
		}

		employeeDAO.delete(funcionarioRemover);
		logger.info("Operador de máquina ID: {} removida com sucesso.", id);
	}

	public Map<String, Object> obterDadosRelatorio() {
		logger.debug("Coletando dados puros para relatório");
		List<Employee> funcionarios = employeeDAO.findAll();

		if (funcionarios.isEmpty()) {
			logger.warn("Não há dados para o relatório");
			throw new BusinessRuleException("Nenhum funcionário registrado para gerar relatório.");
		}

		long contGerente = funcionarios.stream().filter(g -> g instanceof Manager).count();
		long contOperadorMaquina = funcionarios.stream().filter(o -> o instanceof OperatorMachine).count();

		List<OperatorMachine> operadores = funcionarios.stream().filter(OperatorMachine.class::isInstance)
				.map(OperatorMachine.class::cast).collect(Collectors.toList());

		long operadoresAtivos = operadores.stream().filter(
				o -> o.getMaquinaAlocada() != null && o.getMaquinaAlocada().getStatus() == StatusMachine.OPERANDO)
				.count();

		long operadoresInativos = operadores.stream().filter(
				o -> o.getMaquinaAlocada() != null && o.getMaquinaAlocada().getStatus() == StatusMachine.EM_MANUTENCAO
						|| o.getMaquinaAlocada().getStatus() == StatusMachine.PARADA)
				.count();

		List<Manager> gerentes = funcionarios.stream().filter(Manager.class::isInstance).map(Manager.class::cast)
				.collect(Collectors.toList());

		Set<String> setores = gerentes.stream().map(setor -> setor.getAreaResponsavel()).collect(Collectors.toSet());

		Map<String, Object> dadosRelatorio = new HashMap<>();
		dadosRelatorio.put("totalFuncionarios", funcionarios.size());
		dadosRelatorio.put("totalGerentes", contGerente);
		dadosRelatorio.put("totalOperadores", contOperadorMaquina);
		dadosRelatorio.put("operadoresEmMaquinasAtivas", operadoresAtivos);
		dadosRelatorio.put("operadoresEmInatividade", operadoresInativos);
		dadosRelatorio.put("setores", setores);
		logger.info("Dados do relatório coletados com sucesso.");
		return dadosRelatorio;

	}

	@Override
	public String gerarRelatorio() {

		try {
			Map<String, Object> dados = obterDadosRelatorio();
			StringBuilder sb = new StringBuilder();
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").withZone(ZoneId.systemDefault());
			sb.append("--- Relatório de Funcionários ---\n");
			sb.append("Data de Geração:").append(fmt.format(LocalDateTime.now()));

			sb.append("- Total de funcionários: ").append(dados.get("totalFuncionarios"));
			sb.append("\n- Gerentes: ").append(dados.get("totalGerentes"));
			sb.append("\n- Operadores de máquinas: ").append(dados.get("totalOperadores"));
			sb.append("\n\nSituação dos operadores: ");
			sb.append("\n- Operadores em máquinas ativas (OPERANDO): ").append(dados.get("operadoresEmMaquinasAtivas"));
			sb.append("\n- Operadores em máquinas inativas (PARADA/MANUTENÇÃO): ")
					.append(dados.get("operadoresEmInatividade"));
			sb.append("\n- Setores ").append(dados.get("setores"));
			sb.append("\n\nGerência por área:");

			return sb.toString();

		} catch (BusinessRuleException e) {
			logger.warn("Operação falhou: {}", e.getMessage());
			return e.getMessage();
		}

	}

}
