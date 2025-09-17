package entities;

public class Product {

	private int id;
	private double precoCusto;
	private String nome;
	private String descricao;
	private static int proximoId = 1;

	public Product() {

	}

	public Product(double precoCusto, String nome, String descricao) {

		this.precoCusto = precoCusto;
		this.nome = nome;
		this.descricao = descricao;
		this.id = proximoId++;
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
	
	public String getDescricao() {
		return descricao;
	}
	public void setDecricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", precoCusto=" + precoCusto + ", nome=" + nome + ", descricao=" + descricao + "]";
	}
	
	
}
