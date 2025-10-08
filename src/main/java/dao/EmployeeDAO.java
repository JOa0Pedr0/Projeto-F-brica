package dao;

import java.util.List;

import entities.Employee;
import jakarta.persistence.EntityManager;
import util.JPAUtil;

public class EmployeeDAO {

	public void cadastrar(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();

		if (funcionario == null) {
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(funcionario);
			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public Employee buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			return em.find(Employee.class, id);
		} finally {
			em.close();
		}
	}

	public List<Employee> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			String jpql = "SELECT e FROM Employee e";
			return em.createQuery(jpql, Employee.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();

		if (funcionario == null) {
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.merge(funcionario);
			em.getTransaction().commit();

		} finally {
			em.close();
		}
	}

	public void remover(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();
		if (funcionario == null) {
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.remove(em.merge(funcionario));
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
}
