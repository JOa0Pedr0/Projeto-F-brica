package dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import entities.Employee;
import jakarta.persistence.EntityManager;
import service.exceptions.ResourceNotFoundException;
import util.JPAUtil;

public class EmployeeDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeDAO.class);

	public void cadastrar(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();

		if (funcionario == null) {
			logger.error("Tentativa de cadastrar funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(funcionario);
			em.getTransaction().commit();
			logger.info("Funcionário cadastrado com sucesso. Novo ID: {}", funcionario.getId());

		} finally {
			em.close();
		}
	}

	public Employee buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		logger.debug("Buscando funcionário com ID: {}", id);
		try {
			Employee funcionario = em.find(Employee.class, id);

			if (funcionario == null) {
				logger.warn("Funcionário com ID {} não encontrado.", id);
				throw new ResourceNotFoundException("Funcionário não encontrado ID: " + id);
				
			}
			logger.debug("Funcionário com ID: {} encontrado", funcionario.getId());
			return funcionario;
			
			
		} finally {
			em.close();
		}
	}

	public List<Employee> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			logger.debug("Executando uma query JPQL: SELECT e FROM Employee e");
			String jpql = "SELECT e FROM Employee e";
			return em.createQuery(jpql, Employee.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();

		if (funcionario == null) {
			logger.error("Tentativa de atualizar funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.merge(funcionario);
			em.getTransaction().commit();
			logger.info("Funcionário com ID: {} atualizado com sucesso.", funcionario.getId());

		} finally {
			em.close();
		}
	}

	public void remover(Employee funcionario) {
		EntityManager em = JPAUtil.getEntityManager();
		if (funcionario == null) {
			logger.error("Tentativa de excluir um funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.remove(em.merge(funcionario));
			em.getTransaction().commit();
			logger.info("Funcionário com ID: {} removido com sucesso.", funcionario.getId());
		} finally {
			em.close();
		}
	}
}
