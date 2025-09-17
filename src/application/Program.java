package application;

import entities.Product;

public class Program {

	public static void main(String[] args) {
		
		Product parafuso = new Product(0.50,"Parafuso Sextavado", "Aço carbono 10mm");
		
		Product porca = new Product(0.75,"Porca Autotravante", "Aço inox 10mm");

		System.out.println(parafuso);
		System.out.println(porca);
	}

}
