package dao;

import java.util.List;

import entities.Machine;
import jakarta.persistence.EntityManager;
import service.exceptions.ResourceNotFoundException;
import util.JPAUtil;

public class MachineDAO {

	public void cadastrar(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(maquina);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public Machine buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			Machine maquina = em.find(Machine.class, id);

			if (maquina == null) {
				throw new ResourceNotFoundException("Máquina não encontrada para o ID " + id);
			}
			return maquina;
		} finally {
			em.close();
		}
	}

	public List<Machine> listarTodos() {
		EntityManager em = JPAUtil.getEntityManager();
		try {
			String jpql = "SELECT m FROM Machine m";
			return em.createQuery(jpql, Machine.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.merge(maquina);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public void remover(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
		if (maquina == null) {
			throw new IllegalArgumentException("Máquina não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(maquina));
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

}
