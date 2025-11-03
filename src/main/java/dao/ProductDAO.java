package dao;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import entities.Product;
import jakarta.persistence.EntityManager;
import service.exceptions.ResourceNotFoundException;
import util.JPAUtil;

public class ProductDAO {

	private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

	public void cadastrar(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			logger.error("Tentativa de cadastrar produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(produto);
			em.getTransaction().commit();
			logger.info("Produto cadastrado com sucesso. Novo ID: {}", produto.getId());
		} finally {
			em.close();
		}
	}

	public Product buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		logger.debug("Buscando produto com ID: {}", id);

		try {
			Product produto = em.find(Product.class, id);

			if (produto == null) {
				logger.warn("Produto com ID: {} não encontrado.", id );
				throw new ResourceNotFoundException("Produto não encontrado ID " + id);
			}
			logger.debug("Produto com ID: {} encontrado.", produto.getId());
			return produto;

		} finally {
			em.close();
		}
	}

	public List<Product> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			logger.debug("Executando querry JQPL: SELECT p FROM Product p");
			String jpql = "SELECT p FROM Product p";
			return em.createQuery(jpql, Product.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			logger.error("Tentativa de atualizar produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.merge(produto);
			em.getTransaction().commit();
			logger.info("Produto com ID: {} atualizado com sucesso.", produto.getId());
		} finally {
			em.close();
		}
	}

	public void remover(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			logger.error("Tentativa de remover produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(produto));
			em.getTransaction().commit();
			logger.info("Produto do ID: {} removido com sucesso.", produto.getId());
		} finally {
			em.close();
		}

	}
}
