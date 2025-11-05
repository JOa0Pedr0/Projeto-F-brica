package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import entities.ProductionOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import service.exceptions.ResourceNotFoundException;

@Repository
public class ProductionOrderDAO {

	private static final Logger logger = LoggerFactory.getLogger(ProductionOrderDAO.class);

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void cadastrar(ProductionOrder ordemDeProducao) {

		if (ordemDeProducao == null) {
			logger.error("Tentativa de cadastrar Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		em.persist(ordemDeProducao);

		logger.info("Ordem de Produção cadastrada com sucesso. Novo ID: {}.", ordemDeProducao.getId());

	}

	public ProductionOrder buscarPorId(int id) {

		logger.debug("Buscando Ordem de Produção com ID: {}.", id);

		ProductionOrder productionOrder = em.find(ProductionOrder.class, id);

		if (productionOrder == null) {
			logger.warn("Ordem de Produção com ID: {} não encontrada", id);
			throw new ResourceNotFoundException("Ordem de produção não encontrada ID: " + id);
		}
		logger.debug("Ordem de Produção ID: {} encontrada", productionOrder.getId());
		return productionOrder;

	}

	public List<ProductionOrder> listarTodas() {

		logger.debug("Executando query JPQL: SELECT p FROM ProductionOrder p");
		String jpql = "SELECT p FROM ProductionOrder p";
		return em.createQuery(jpql, ProductionOrder.class).getResultList();

	}

	@Transactional
	public void atualizar(ProductionOrder ordemDeProducao) {

		if (ordemDeProducao == null) {
			logger.error("Tentativa de atualizar Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		em.merge(ordemDeProducao);

		logger.info("Ordem de Produção com ID: {} atualizada com sucesso.", ordemDeProducao.getId());

	}

	@Transactional
	public void remover(ProductionOrder ordemDeProducao) {

		if (ordemDeProducao == null) {
			logger.error("Tentativa de remover Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		em.remove(em.merge(ordemDeProducao));

		logger.info("Ordem de Produção com ID: {} removido com sucesso.", ordemDeProducao.getId());

	}

	public boolean existeOrdemComProdutoId(int produtoId) {

		logger.debug(
				"Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.produtoASerProduzido.id = :produtoId ");

		String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.produtoASerProduzido.id = :produtoId";
		Long count = em.createQuery(jpql, Long.class).setParameter("produtoId", produtoId).getSingleResult();
		return count > 0;

	}

	public boolean existeOrdemComMaquinaId(int maquinaId) {

		logger.debug(
				"Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.maquinaDesignada.id = :maquinaId");

		String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.maquinaDesignada.id = :maquinaId";
		Long count = em.createQuery(jpql, Long.class).setParameter("maquinaId", maquinaId).getSingleResult();
		return count > 0;

	}

	public boolean existeOrdemComOperadorId(int operadorId) {

		logger.debug(
				"Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.operadorResponsavel.id = :operadorId");

		String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.operadorResponsavel.id = :operadorId";
		Long count = em.createQuery(jpql, Long.class).setParameter("operadorId", operadorId).getSingleResult();
		return count > 0;

	}

}
