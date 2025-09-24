package service;

import java.util.ArrayList;
import java.util.List;

import entities.Product;
import service.exceptions.ResourceNotFoundException;

public class ProductService {

	private List<Product> produtos = new ArrayList();

	public void adicionarProduto(Product produto) {

		if (produto == null) {
			throw new IllegalArgumentException("Não é possível cadastrar um produto nulo.");
		}
		produtos.add(produto);
		System.out.println("Produto " + produto.getNome() + " adicionado no estoque.");
	}

	public List<Product> listarTodosProdutos() {
		return produtos;
	}

	public Product buscarPorId(int id) {
		for (Product produto : produtos) {
			if (produto.getId() == id) {
				return produto;
			}
		}
		throw new ResourceNotFoundException("Produto não encontrado. Id: " + id);
	}
}
