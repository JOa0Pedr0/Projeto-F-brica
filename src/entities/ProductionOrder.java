package entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProductionOrder {

	private int id;
	private Product produtoASerProduzido;
	private int quantidade;
	private Machine maquinaDesignada;
	private OperatorMachine operadorResponsavel;
	private LocalDateTime dataDeCriacao;
	private int proximoId = 1;
	
	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").withZone(ZoneId.systemDefault());
	
	public ProductionOrder() {
		this.id = proximoId++;
	}

	public ProductionOrder(Product produtoASerProduzido, int quantidade, Machine maquinaDesignada,
			OperatorMachine operadorResponsavel) {

		this.produtoASerProduzido = produtoASerProduzido;
		this.quantidade = quantidade;
		this.maquinaDesignada = maquinaDesignada;
		this.operadorResponsavel = operadorResponsavel;

		this.id = proximoId++;
		this.dataDeCriacao =  LocalDateTime.now();

	}
	
	public int getId() {
		return id;
	}
	public Product getProdutoASerProduzido() {
		return produtoASerProduzido;
	}
	public int getQuantidade() {
		return quantidade;
	}
	public Machine getMaquinaDesignada() {
		return maquinaDesignada;
	}
	public OperatorMachine getOperadorResponsavel() {
		return operadorResponsavel;
	}
	public LocalDateTime getDataDaCriacao() {
		return dataDeCriacao;
	}

	@Override
	public String toString() {
		return "ProductionOrder [id=" + id + ", produtoASerProduzido=" + produtoASerProduzido.getNome() + ", quantidade="
				+ quantidade + ", maquinaDesignada=" + maquinaDesignada.getModelo() + ", operadorResponsavel=" + operadorResponsavel.getNome()
				+ ", dataDeCriacao=" + fmt.format(dataDeCriacao) + "]";
	}

}
