package dao;

import java.util.List;

import entities.ProductionOrder;
import jakarta.persistence.EntityManager;
import service.exceptions.BusinessRuleException;
import util.JPAUtil;

public class ProductionOrderDAO {

	public void cadastrar(ProductionOrder ordemDeProducao) {

		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(ordemDeProducao);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public ProductionOrder buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();
	
		try {
			ProductionOrder productionOrder =  em.find(ProductionOrder.class, id);
			
			if(productionOrder == null) {
				
				throw new BusinessRuleException("Ordem de produção não encontrada ID: " + id);
			}
			
			return productionOrder;
		} finally {
			em.close();
		}
	}

	public List<ProductionOrder> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			String jpql = "SELECT p FROM ProductionOrder p";
			return em.createQuery(jpql, ProductionOrder.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(ProductionOrder ordemDeProducao) {
		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.merge(ordemDeProducao);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public void remover(ProductionOrder ordemDeProducao) {
		EntityManager em = JPAUtil.getEntityManager();

		if (ordemDeProducao == null) {
			throw new IllegalArgumentException("Ordem de serviço não pode ser nulo.");
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(ordemDeProducao));
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}
}
