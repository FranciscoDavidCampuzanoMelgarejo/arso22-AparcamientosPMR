package ciudades.servicio;

import org.example.ciudades.Parking;

public class DistanciaParkingSitio {

	private Parking parking;
	private double distancia;

	public DistanciaParkingSitio() {

	}

	public DistanciaParkingSitio(Parking parking, double distancia) {
		this.parking = parking;
		this.distancia = distancia;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	@Override
	public String toString() {
		return "DistanciaParkingSitio [parking=" + parking + ", distancia=" + distancia + "]";
	}

}
