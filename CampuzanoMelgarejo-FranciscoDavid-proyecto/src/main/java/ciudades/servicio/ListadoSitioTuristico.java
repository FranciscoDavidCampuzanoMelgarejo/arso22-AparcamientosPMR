package ciudades.servicio;

import java.util.LinkedList;

public class ListadoSitioTuristico {

	public static class SitioTuristicoResumen {

		private String nombre;
		private String resumen;

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getResumen() {
			return resumen;
		}

		public void setResumen(String resumen) {
			this.resumen = resumen;
		}

		@Override
		public String toString() {
			return "SitioTuristicoResumen [nombre=" + nombre + ", resumen=" + resumen + "]";
		}

	}

	private LinkedList<SitioTuristicoResumen> resumenSitiosTuristicos = new LinkedList<>();

	public LinkedList<SitioTuristicoResumen> getResumenSitiosTuristicos() {
		return resumenSitiosTuristicos;
	}

	public void setResumenSitiosTuristicos(LinkedList<SitioTuristicoResumen> resumenSitiosTuristicos) {
		this.resumenSitiosTuristicos = resumenSitiosTuristicos;
	}

	@Override
	public String toString() {
		return "ListadoSitioTuristico [resumenSitiosTuristicos=" + resumenSitiosTuristicos + "]";
	}

}
