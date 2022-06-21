package ciudades.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
public class ParkingResumen {

	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "opinionResumen", propOrder = {
		    "valoraciones",
		    "calificacionMedia",
		    "urlOpinion"
		})
	public static class OpinionResumen {
		private String urlOpinion;
		private int valoraciones;
		private double calificacionMedia;

		public String getUrlOpinion() {
			return urlOpinion;
		}

		public void setUrlOpinion(String urlOpinion) {
			this.urlOpinion = urlOpinion;
		}

		public int getValoraciones() {
			return valoraciones;
		}

		public void setValoraciones(int valoraciones) {
			this.valoraciones = valoraciones;
		}

		public double getCalificacionMedia() {
			return calificacionMedia;
		}

		public void setCalificacionMedia(double calificacionMedia) {
			this.calificacionMedia = calificacionMedia;
		}

	}

	private String direccion;
	private double latitud;
	private double longitud;
	private OpinionResumen opinion;

	public ParkingResumen() {

	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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

	public OpinionResumen getOpinion() {
		return opinion;
	}

	public void setOpinion(OpinionResumen opinion) {
		this.opinion = opinion;
	}

}
