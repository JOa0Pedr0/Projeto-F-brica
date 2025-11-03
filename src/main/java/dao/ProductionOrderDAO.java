package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.ProductionOrder;
import jakarta.persistence.EntityManager;
import service.exceptions.ResourceNotFoundException;
import util.JPAUtil;

public class ProductionOrderDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductionOrderDAO.class);

	public void cadastrar(ProductionOrder ordemDeProducao) {

		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			logger.error("Tentativa de cadastrar Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(ordemDeProducao);
			em.getTransaction().commit();
			logger.info("Ordem de Produção cadastrada com sucesso. Novo ID: {}.", ordemDeProducao.getId());
		} finally {
			em.close();
		}
	}

	public ProductionOrder buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
		logger.debug("Buscando Ordem de Produção com ID: {}.", id);

		try {
			ProductionOrder productionOrder = em.find(ProductionOrder.class, id);

			if (productionOrder == null) {
				logger.warn("Ordem de Produção com ID: {} não encontrada", id);
				throw new ResourceNotFoundException("Ordem de produção não encontrada ID: " + id);
			}
			logger.debug("Ordem de Produção ID: {} encontrada", productionOrder.getId());
			return productionOrder;
		} finally {
			em.close();
		}
	}

	public List<ProductionOrder> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			logger.debug("Executando query JPQL: SELECT p FROM ProductionOrder p");
			String jpql = "SELECT p FROM ProductionOrder p";
			return em.createQuery(jpql, ProductionOrder.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(ProductionOrder ordemDeProducao) {
		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			logger.error("Tentativa de atualizar Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.merge(ordemDeProducao);
			em.getTransaction().commit();
			logger.info("Ordem de Produção com ID: {} atualizada com sucesso.", ordemDeProducao.getId());
		} finally {
			em.close();
		}
	}

	public void remover(ProductionOrder ordemDeProducao) {
		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			logger.error("Tentativa de remover Ordem de Produção nula.");
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(ordemDeProducao));
			em.getTransaction().commit();
			logger.info("Ordem de Produção com ID: {} removido com sucesso.", ordemDeProducao.getId());
		} finally {
			em.close();
		}
	}

	public boolean existeOrdemComProdutoId(int produtoId) {
		EntityManager em = JPAUtil.getEntityManager();
		logger.debug("Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.produtoASerProduzido.id = :produtoId ");
		try {
			
			String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.produtoASerProduzido.id = :produtoId";
			Long count = em.createQuery(jpql, Long.class).setParameter("produtoId", produtoId).getSingleResult();
			return count > 0;
		} finally {
			em.close();
		}
	}
	
	public boolean existeOrdemComMaquinaId(int maquinaId) {
        EntityManager em = JPAUtil.getEntityManager();
        logger.debug("Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.maquinaDesignada.id = :maquinaId");
        try {
            String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.maquinaDesignada.id = :maquinaId";
            Long count = em.createQuery(jpql, Long.class)
                           .setParameter("maquinaId", maquinaId)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
	
	public boolean existeOrdemComOperadorId(int operadorId) {
        EntityManager em = JPAUtil.getEntityManager();
        logger.debug("Executando query JPQL: SELECT COUNT(o) FROM ProductionOrder o WHERE o.operadorResponsavel.id = :operadorId");
        try {
           
            String jpql = "SELECT COUNT(o) FROM ProductionOrder o WHERE o.operadorResponsavel.id = :operadorId";
            Long count = em.createQuery(jpql, Long.class)
                           .setParameter("operadorId", operadorId)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

}
