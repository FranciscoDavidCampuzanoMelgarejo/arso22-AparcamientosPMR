package ciudades.servicio;

import java.util.LinkedList;

//Clase que contiene una lista de resumenes de cada ciudad
public class ListadoCiudades {

	// Clase que representa un resumen de una ciudad. No incluye ni los sitios de
	// interes
	// ni los aparcamientos
	public static class CiudadResumen {

		private String id;
		private String nombre;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public String toString() {
			return "CiudadResumen [id=" + id + ", nombre=" + nombre + "]";
		}

	}

	private LinkedList<CiudadResumen> resumenCiudades = new LinkedList<>();

	public LinkedList<CiudadResumen> getResumenCiudades() {
		return resumenCiudades;
	}

	public void setResumenCiudades(LinkedList<CiudadResumen> resumenCiudades) {
		this.resumenCiudades = resumenCiudades;
	}

	@Override
	public String toString() {
		return "ListadoCiudades [resumenCiudades=" + resumenCiudades + "]";
	}

}
