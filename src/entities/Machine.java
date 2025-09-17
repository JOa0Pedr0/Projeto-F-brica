package entities;

public class Machine {
	
	private int id;
	private String modelo;
	private String status;
	private static int proximoId = 1;
	
	public Machine() {
		
	}
	
	public Machine(String modelo, String status) {
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
	
	public String getStatus(){
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Machine [id=" + id + ", modelo=" + modelo + ", status=" + status + "]";
	}
	
	
	
}
