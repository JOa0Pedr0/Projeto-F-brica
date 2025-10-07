package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "maquinas")
public class Machine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	
	private String modelo;
	
	@Enumerated(EnumType.STRING)
	private StatusMachine status;

	public Machine() {

	}

	public Machine(String modelo, StatusMachine status) {
		this.modelo = modelo;
		this.status = status;

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

	public StatusMachine getStatus() {
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
