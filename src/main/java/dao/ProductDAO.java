package dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {

}
