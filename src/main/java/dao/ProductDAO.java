package dao;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import entities.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import service.exceptions.ResourceNotFoundException;


@Repository
public class ProductDAO {

	private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void cadastrar(Product produto) {

		if (produto == null) {
			logger.error("Tentativa de cadastrar produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}

		em.persist(produto);

		logger.info("Produto cadastrado com sucesso. Novo ID: {}", produto.getId());
	}

	public Product buscarPorId(int id) {

		logger.debug("Buscando produto com ID: {}", id);

		Product produto = em.find(Product.class, id);

		if (produto == null) {
			logger.warn("Produto com ID: {} não encontrado.", id);
			throw new ResourceNotFoundException("Produto não encontrado ID " + id);
		}
		logger.debug("Produto com ID: {} encontrado.", produto.getId());
		return produto;

	}

	public List<Product> listarTodas() {

		logger.debug("Executando querry JQPL: SELECT p FROM Product p");
		String jpql = "SELECT p FROM Product p";
		return em.createQuery(jpql, Product.class).getResultList();

	}

	@Transactional
	public Product atualizar(Product produto) {

		if (produto == null) {
			logger.error("Tentativa de atualizar produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}

		em.merge(produto);

		logger.info("Produto com ID: {} atualizado com sucesso.", produto.getId());
		return produto;
	}

	@Transactional
	public void remover(Product produto) {

		if (produto == null) {
			logger.error("Tentativa de remover produto nulo.");
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}

		em.remove(em.merge(produto));

		logger.info("Produto do ID: {} removido com sucesso.", produto.getId());

	}
}
