package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Machine;
import jakarta.persistence.EntityManager;
import service.exceptions.ResourceNotFoundException;
import util.JPAUtil;

public class MachineDAO {

	private static final Logger logger = LoggerFactory.getLogger(MachineDAO.class);

	public void cadastrar(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			logger.error("Tentativa de cadastrar uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(maquina);
			em.getTransaction().commit();
			logger.info("Máquina cadastrada com suceso. Novo ID: {}", maquina.getId());
		} finally {
			em.close();
		}
	}

	public Machine buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		logger.debug("Buscando máquina com ID: {}", id);
		try {
			Machine maquina = em.find(Machine.class, id);

			if (maquina == null) {
				logger.warn("Máquina com ID: {} não encontrada. ", id);
				throw new ResourceNotFoundException("Máquina não encontrada para o ID " + id);

			}
			logger.debug("Máquina encontrada ID: {}", maquina);
			return maquina;
		} finally {
			em.close();
		}
	}

	public List<Machine> listarTodos() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			logger.debug("Executando query JPQL: SELECT m FROM Machine Machine m");
			String jpql = "SELECT m FROM Machine m";
			return em.createQuery(jpql, Machine.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			logger.error("Tentativa de atualizar uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.merge(maquina);
			em.getTransaction().commit();
			logger.info("Máquina com ID: {} atualizada com sucesso.", maquina.getId());
		} finally {
			em.close();
		}
	}

	public void remover(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			logger.error("Tentativa de remover uma máquina nula.");
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(maquina));
			em.getTransaction().commit();
			logger.info("Máquina com ID: {} removida com sucesso.", maquina.getId());
		} finally {
			em.close();
		}
	}

}
