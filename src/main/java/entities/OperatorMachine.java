package entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("OPERADOR")
public class OperatorMachine extends Employee {

	@ManyToOne
	@JoinColumn(name = "maquina_alocada_id")
	private Machine maquinaAlocada;
	
	public OperatorMachine() {
		super();
	}
	public OperatorMachine(String nome, String matricula, Machine maquinaAlocada) {
		super(nome, matricula);
		this.maquinaAlocada = maquinaAlocada;
	}
	public Machine getMaquinaAlocada() {
		return maquinaAlocada;
	}
	public void setMaquinaAlocada(Machine maquinaAlocada) {
		this.maquinaAlocada = maquinaAlocada;
	}
	@Override
	public String toString() {
		return super.toString() + ", OperatorMachine [maquinaAlocada=" + maquinaAlocada + "]";
	}	
}
