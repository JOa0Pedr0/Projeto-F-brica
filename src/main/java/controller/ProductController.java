package controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import entities.Product;
import service.ProductService;

@RestController
@RequestMapping("/api/produtos")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Product adicionar(@RequestBody Product produto) {
		return productService.adicionarProduto(produto);
	}
	
	@GetMapping
	public List<Product> listarTodos(){
		return productService.listarTodosProdutos();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> buscarPorI(@PathVariable int id){
		Product produto = productService.buscarPorId(id);
		return ResponseEntity.ok(produto);
	}
	
	@PutMapping("/{id}")
	public Product atualizar(@PathVariable int id, @RequestBody Product produto) {
		return productService.atualizar(id, produto);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable int id){
		productService.remover(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/relatorio")
	public ResponseEntity<Map<String,Object>> getRelatorio(){
		Map<String, Object> dados = productService.obterDadosRelatorio();
		return ResponseEntity.ok(dados);
	}
}
