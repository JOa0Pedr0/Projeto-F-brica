package dao;

import java.util.List;

import entities.Product;
import jakarta.persistence.EntityManager;
import util.JPAUtil;

public class ProductDAO {

	public void cadastrar(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.persist(produto);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public Product buscarPorId(int id) {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			return em.find(Product.class, id);
		} finally {
			em.close();
		}
	}

	public List<Product> listarTodas() {
		EntityManager em = JPAUtil.getEntityManager();

		try {
			String jpql = "SELECT p FROM Product p";
			return em.createQuery(jpql, Product.class).getResultList();
		} finally {
			em.close();
		}
	}

	public void atualizar(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			throw new IllegalArgumentException("Produto não pode ser nulo.");
		}
		try {
			em.getTransaction().begin();
			em.merge(produto);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	public void remover(Product produto) {
		EntityManager em = JPAUtil.getEntityManager();

		if (produto == null) {
			throw new IllegalArgumentException();
		}

		try {
			em.getTransaction().begin();
			em.remove(em.merge(produto));
			em.getTransaction().commit();
		} finally {
			em.close();
		}

	}
}
