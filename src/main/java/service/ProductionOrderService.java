package service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entities.Machine;
import entities.OperatorMachine;
import entities.Product;
import entities.ProductionOrder;
import entities.StatusMachine;
import interfaces.Reportable;
import service.exceptions.BusinessRuleException;
import service.exceptions.ResourceNotFoundException;

public class ProductionOrderService implements Reportable {

	private List<ProductionOrder> ordensDeProducao = new ArrayList<>();
	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyy hh:mm");

	public void criarNovaOrdem(Product produto, int quantidade, Machine maquina, OperatorMachine operador) {
		if (produto == null || maquina == null || operador == null) {
			throw new IllegalArgumentException("Erro ao criar ordem: produto, máquina ou operador inválido (nulo).");
		}
		if (maquina.getStatus() != StatusMachine.OPERANDO) {
			throw new BusinessRuleException(
					"A máquina não se encontra disponível para criar uma nova ordem de produção.");
	
		}
		ProductionOrder ordemDeProducao = new ProductionOrder(produto, quantidade, maquina, operador);

		ordensDeProducao.add(ordemDeProducao);
		System.out.println("Ordem de Produção Adicionada.");
		
	}
	
	public List<ProductionOrder> listarTodas(){
		return ordensDeProducao;
	}
	
	public ProductionOrder buscarPorId(int id) {
		for(ProductionOrder ordemProducao : ordensDeProducao) {
			if(ordemProducao.getId() == id) {
				return ordemProducao;
			}
		}
		
		throw new ResourceNotFoundException("Ordem de produção não encontrada: " + id);
	}

	@Override
	public String gerarRelatorio() {
		
		if(ordensDeProducao.isEmpty()) {
			return "Nenhuma Ordem de Produção registrada para gerar relatório ";
			
		}
		StringBuilder sb = new StringBuilder();
		sb.append("--- Relatório de Carga de Trabalho ---");
		sb.append("\nData de Geração: ").append(LocalDateTime.now().format(fmt));
		sb.append("\n=======================================================");
		sb.append("\nTotal de Ordens Registradas: ").append(ordensDeProducao.size());
		sb.append("\nVolume de Produção por Produto:");
		Map<String, Integer> volumePorProduto = ordensDeProducao.stream()
	            .collect(Collectors.groupingBy(
	                o -> o.getProdutoASerProduzido().getNome(),
	                Collectors.summingInt(ProductionOrder::getQuantidade)
	            ));
		
		volumePorProduto.forEach((nomeProduto, quantidade) ->
        sb.append("\n- ").append(nomeProduto).append(": ").append(quantidade).append(" unidades\n")
    );
		
		sb.append("\nRecursos Atualmente Alocados em Ordens:");
		for(ProductionOrder ordemProducao : ordensDeProducao) {
			sb.append("\n- Máquina #").append(ordemProducao.getMaquinaDesignada().getId());
			sb.append("(").append(ordemProducao.getMaquinaDesignada().getModelo()).append(")");
		}
		
		sb.append("\\nnOperadores Alocados:");
		for(ProductionOrder ordemProducao : ordensDeProducao) {
			sb.append("\n- Operador: ").append(ordemProducao.getOperadorResponsavel().getNome());
			sb.append(" (Matrícula: ").append(ordemProducao.getOperadorResponsavel().getMatricula()).append(")");
		}
		sb.append("\n--- Fim do Relatório ---");
		
		return sb.toString();
	}

}
