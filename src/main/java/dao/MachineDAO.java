package dao;

import java.util.List;

import entities.Machine;
import jakarta.persistence.EntityManager;
import util.JPAUtil;

public class MachineDAO {

	public void cadastrar(Machine maquina) {
		EntityManager em = JPAUtil.getEntityManager();
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
			return em.find(Machine.class, id);
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
		
		try {
			em.getTransaction().begin();
			em.remove(em.merge(maquina));
			em.getTransaction().commit();
		}finally {
			em.close();
		}
	}

}
