package controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import entities.ProductionOrder;
import service.ProductionOrderService;

@RestController
@RequestMapping("/api/ordens")
public class ProductionOrderController {

	@Autowired
	private ProductionOrderService productionOrderService;
	
	@GetMapping
	public List<ProductionOrder> listarTodas(){
		return productionOrderService.listarTodas();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductionOrder> buscarPorId(@PathVariable int id){
		ProductionOrder ordem = productionOrderService.buscarPorId(id);
		return ResponseEntity.ok(ordem);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductionOrder criar(@RequestBody ProductionOrder novaOrdem) {
		
		return productionOrderService.criarNovaOrdem(novaOrdem);
		
	}
	
	@PutMapping("/{id}/iniciar")
	public ResponseEntity<ProductionOrder> iniciarOrdem(@PathVariable int id){
		ProductionOrder ordemAtualizada = productionOrderService.iniciarOrdem(id);
		return ResponseEntity.ok(ordemAtualizada);
	}
	
	@PutMapping("/{id}/concluir")
	public ResponseEntity<ProductionOrder> concluirOrdem(@PathVariable int id){
		ProductionOrder ordemAtualizada = productionOrderService.concluirOrdem(id);
		return ResponseEntity.ok(ordemAtualizada);
		
	}
	@PutMapping("/{id}/cancelar")
	public ResponseEntity<ProductionOrder> cancelarOrdem(@PathVariable int id){
		ProductionOrder ordemCancelar = productionOrderService.cancelarOrdem(id);
		return ResponseEntity.ok(ordemCancelar);
	}
	
	@GetMapping("/relatorio")
	public ResponseEntity<Map<String, Object>> getRelatorio(){
		Map<String, Object> dados = productionOrderService.obterDadosRelatorio();
		return ResponseEntity.ok(dados);
	}
}
