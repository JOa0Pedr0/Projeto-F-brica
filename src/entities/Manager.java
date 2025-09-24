package entities;

public class Manager extends Employee {
	private String areaResponsavel;
	
	public Manager() {
		super();
	}
	
	public Manager(String nome, String matricula, String areaResponsavel) {
		super(nome, matricula);
		this.areaResponsavel = areaResponsavel;
	}

	public String getAreaResponsavel() {
		return areaResponsavel;
	}

	public void setAreaResponsavel(String areaResponsavel) {
		this.areaResponsavel = areaResponsavel;
	}

	@Override
	public String toString() {
		return super.toString() +", Manager [areaResponsavel=" + areaResponsavel + "]";
	}
}
