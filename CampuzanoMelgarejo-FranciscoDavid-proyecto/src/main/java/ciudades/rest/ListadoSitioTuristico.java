package ciudades.rest;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
public class ListadoSitioTuristico {

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "sitioTuristicoResumen", propOrder = {
		    "titulo",
		    "resumen",
		    "latitud",
		    "longitud",
		    "wikipedia",
		    "url"
		})
	public static class SitioTuristicoResumen {

		private String titulo;
		private String resumen;
		private double latitud;
		private double longitud;
		private String wikipedia;

		private String url;

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		public String getResumen() {
			return resumen;
		}

		public void setResumen(String resumen) {
			this.resumen = resumen;
		}

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

		public String getWikipedia() {
			return wikipedia;
		}

		public void setWikipedia(String wikipedia) {
			this.wikipedia = wikipedia;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	private List<SitioTuristicoResumen> sitiosResumen = new LinkedList<>();

	public List<SitioTuristicoResumen> getSitiosResumen() {
		return sitiosResumen;
	}

	public void setSitiosResumen(List<SitioTuristicoResumen> sitiosResumen) {
		this.sitiosResumen = sitiosResumen;
	}

}
