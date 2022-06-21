package ciudades.rest;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListadoDistanciaParkingSitio {

	public static class DistanciaParkingSitioResumen {

		private double latitud;
		private double longitud;
		private double distancia;

		private String url;

		public double getLatitud() {
			return latitud;
		}

		public void setLatitud(double latitud) {
			this.latitud = latitud;
		}

		public double getLongitud() {
			return longitud;
		}

		public void setLongitud(double longitud) {
			this.longitud = longitud;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public double getDistancia() {
			return distancia;
		}

		public void setDistancia(double distancia) {
			this.distancia = distancia;
		}
	}

	private List<DistanciaParkingSitioResumen> parkingsResumen = new LinkedList<>();

	public List<DistanciaParkingSitioResumen> getParkings() {
		return parkingsResumen;
	}

	public void setParkings(List<DistanciaParkingSitioResumen> parkings) {
		this.parkingsResumen = parkings;
	}

}
