package entities;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private double precoCusto;
	
	private String nome;
	
	private String descricao;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "maquina_id")
	private Machine maquina;

	
	
	public Product() {
		
	}

	public Product(double precoCusto, String nome, String descricao, Machine maquina) {

		this.precoCusto = precoCusto;
		this.nome = nome;
		this.descricao = descricao;
		this.maquina = maquina;
	}

	public int getId() {
		return id;
	}

	public double getPrecoCusto() {
		return precoCusto;
	}

	public void setPrecoCusto(double precoCusto) {
		this.precoCusto = precoCusto;
	}

	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Machine getMaquina() {
		return maquina;
	}
	
	public void setMaquina(Machine maquina) {
		this.maquina = maquina;
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", precoCusto=" + precoCusto + ", nome=" + nome + ", descricao=" + descricao + ", maquina=" + maquina +"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return id == other.id && Objects.equals(nome, other.nome);
	}
	
	
}
