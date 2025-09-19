package entities;

public class OperatorMachine extends Employee {

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
