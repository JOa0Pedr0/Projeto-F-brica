package dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.OrderStatus;
import entities.ProductionOrder;

@Repository
public interface ProductionOrderDAO extends JpaRepository<ProductionOrder, Integer> {


	@Query("SELECT COUNT(o) FROM ProductionOrder o WHERE o.produtoASerProduzido.id = :produtoId")
	Long countByProdutoId(@Param("produtoId") int produtoId);

	@Query("SELECT COUNT(o) FROM ProductionOrder o WHERE o.operadorResponsavel.id = :operadorId")
	Long countByOperadorId(@Param("operadorId") int operadorId);

	@Query("SELECT COUNT(o) FROM ProductionOrder o WHERE o.maquinaDesignada.id = :maquinaId")
	Long countByMaquinaId(@Param("maquinaId") int maquinaId);

	List<ProductionOrder> findByStatus(OrderStatus status);


}
