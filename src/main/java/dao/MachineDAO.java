package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import entities.Machine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import service.exceptions.ResourceNotFoundException;

@Repository
public class MachineDAO {

	private static final Logger logger = LoggerFactory.getLogger(MachineDAO.class);
	
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void cadastrar(Machine maquina) {

		if (maquina == null) {
			logger.error("Tentativa de cadastrar uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}

		em.persist(maquina);
		logger.info("Máquina cadastrada com suceso. Novo ID: {}", maquina.getId());

	}

	public Machine buscarPorId(int id) {

		logger.debug("Buscando máquina com ID: {}", id);

		Machine maquina = em.find(Machine.class, id);

		if (maquina == null) {
			logger.warn("Máquina com ID: {} não encontrada. ", id);
			throw new ResourceNotFoundException("Máquina não encontrada para o ID " + id);

		}
		logger.debug("Máquina encontrada ID: {}", maquina);
		return maquina;

	}

	public List<Machine> listarTodos() {

		logger.debug("Executando query JPQL: SELECT m FROM Machine Machine m");
		String jpql = "SELECT m FROM Machine m";
		return em.createQuery(jpql, Machine.class).getResultList();
	}

	@Transactional
	public void atualizar(Machine maquina) {

		if (maquina == null) {
			logger.error("Tentativa de atualizar uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}

		em.merge(maquina);

		logger.info("Máquina com ID: {} atualizada com sucesso.", maquina.getId());

	}

	@Transactional
	public void remover(Machine maquina) {

		if (maquina == null) {
			logger.error("Tentativa de remover uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}

		em.remove(em.merge(maquina));

		logger.info("Máquina com ID: {} removida com sucesso.", maquina.getId());

	}

}
