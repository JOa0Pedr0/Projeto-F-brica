package service;

import java.util.ArrayList;
import java.util.List;

import entities.Product;

public class ProductService {
	
	private List<Product> estoque = new ArrayList();
	
	public void adicionarProduto(Product produto) {
		
		if(produto != null) {
			estoque.add(produto);
			System.out.println("Produto " + produto.getNome() + " adicionado no estoque.");
		}
	}
	
	public void listarTodosProdutos() {
		if(estoque.isEmpty()) {
			System.out.println("Ainda não há produto no estoque.");
		}
		System.out.println("Lista de produtos:");
		
		for(Product produto : estoque) {
			System.out.println(produto);
		}
	}
	
	public Product buscarPorId(int id) {
		for(Product produto : estoque) {
			if(produto.getId() == id) {
				return produto;
			}
		}
		return null;
	}
}
