package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entities.Product;
import interfaces.Reportable;
import service.exceptions.ResourceNotFoundException;

public class ProductService implements Reportable {

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
	
	@Override
	public String gerarRelatorio() {
		return "Quantidade de produtos no estoque: " + listarTodosProdutos().size() + 
				"\nValor do estoque: R$ " + listarTodosProdutos().stream().mapToDouble(Product::getPrecoCusto).sum();
	}
}
