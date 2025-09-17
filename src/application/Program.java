package application;

import entities.Product;
import service.ProductService;

public class Program {

	public static void main(String[] args) {
		
		ProductService produtoService = new ProductService();
		
		produtoService.AdicionarProduto(new Product(0.50,"Parafuso Sextavado", "Aço carbono 10mm"));
		
		produtoService.AdicionarProduto(new Product(0.75,"Porca Autotravante", "Aço inox 10mm"));
		
		produtoService.AdicionarProduto(new Product(1.60,"Fita de nylon 300MM","Fita nylon pvc 9nn"));
		
		produtoService.listarTodosProdutos();
		
	}

}
