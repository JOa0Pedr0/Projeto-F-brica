package entities;

public class Machine {
	
	private int id;
	private String modelo;
	private StatusMachine status;
	private static int proximoId = 1;
	
	public Machine() {
		this.id = proximoId++;
	}
	
	public Machine(String modelo, StatusMachine status) {
		this.modelo = modelo;
		this.status = status;
		this.id = proximoId++;
	}
	
	public int getId() {
		return id;	
	}
	
	public String getModelo() {
		return modelo;
	}
	
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	
	public StatusMachine getStatus(){
		return status;
	}
	public void setStatus(StatusMachine status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Machine [id=" + id + ", modelo=" + modelo + ", status=" + status + "]";
	}

	
	
	
	
}
