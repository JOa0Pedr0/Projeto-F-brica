package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import entities.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import service.exceptions.ResourceNotFoundException;

@Repository
public class EmployeeDAO {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeDAO.class);

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void cadastrar(Employee funcionario) {

		if (funcionario == null) {
			logger.error("Tentativa de cadastrar funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}

		em.persist(funcionario);

		logger.info("Funcionário cadastrado com sucesso. Novo ID: {}", funcionario.getId());

	}

	public Employee buscarPorId(int id) {

		logger.debug("Buscando funcionário com ID: {}", id);

		Employee funcionario = em.find(Employee.class, id);

		if (funcionario == null) {
			logger.warn("Funcionário com ID {} não encontrado.", id);
			throw new ResourceNotFoundException("Funcionário não encontrado ID: " + id);

		}
		logger.debug("Funcionário com ID: {} encontrado", funcionario.getId());
		return funcionario;

	}

	public List<Employee> listarTodas() {

		logger.debug("Executando uma query JPQL: SELECT e FROM Employee e");
		String jpql = "SELECT e FROM Employee e";
		return em.createQuery(jpql, Employee.class).getResultList();

	}

	@Transactional
	public void atualizar(Employee funcionario) {

		if (funcionario == null) {
			logger.error("Tentativa de atualizar funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}

		em.merge(funcionario);

		logger.info("Funcionário com ID: {} atualizado com sucesso.", funcionario.getId());

	}

	@Transactional
	public void remover(Employee funcionario) {

		if (funcionario == null) {
			logger.error("Tentativa de excluir um funcionário nulo.");
			throw new IllegalArgumentException("Funcionário não pode ser nulo.");
		}

		em.remove(em.merge(funcionario));

		logger.info("Funcionário com ID: {} removido com sucesso.", funcionario.getId());

	}
}
