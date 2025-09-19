package entities;

public abstract class Employee {
	private int id;
	private String nome;
	private String matricula;
	private static int proximoId;

	public Employee() {
		id = proximoId++;
	}

	public Employee(String nome, String matricula) {
		this.nome = nome;
		this.matricula = matricula;
		id = proximoId++;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", nome=" + nome + ", matricula=" + matricula + "]";
	}

}
