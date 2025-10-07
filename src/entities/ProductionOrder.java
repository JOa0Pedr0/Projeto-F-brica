package entities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordens_de_producao")
public class ProductionOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "produto_id")
	private Product produtoASerProduzido;
	
	private int quantidade;
	
	@ManyToOne
	@JoinColumn(name = "maquina_id")
	private Machine maquinaDesignada;
	
	@ManyToOne
	@JoinColumn(name = "operador_id")
	private OperatorMachine operadorResponsavel;
	private LocalDateTime dataDeCriacao;
	
	
	private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").withZone(ZoneId.systemDefault());
	
	public ProductionOrder() {
		
	}

	public ProductionOrder(Product produtoASerProduzido, int quantidade, Machine maquinaDesignada,
			OperatorMachine operadorResponsavel) {

		this.produtoASerProduzido = produtoASerProduzido;
		this.quantidade = quantidade;
		this.maquinaDesignada = maquinaDesignada;
		this.operadorResponsavel = operadorResponsavel;
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
		String dataFormatada = (dataDeCriacao != null) ? (fmt.format(dataDeCriacao)) : "N/A";
		
		return "ProductionOrder [id=" + id + ", produtoASerProduzido=" + produtoASerProduzido.getNome() + ", quantidade="
				+ quantidade + ", maquinaDesignada=" + maquinaDesignada.getModelo() + ", operadorResponsavel=" + operadorResponsavel.getNome()
				+ ", dataDeCriacao=" + dataFormatada + "]";
	}

}
