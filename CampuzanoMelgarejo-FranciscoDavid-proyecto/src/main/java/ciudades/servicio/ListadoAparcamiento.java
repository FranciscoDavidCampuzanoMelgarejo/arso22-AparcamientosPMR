package ciudades.servicio;

import java.util.LinkedList;

public class ListadoAparcamiento {

	public static class AparcamientoResumen {

		private String nombre;

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		@Override
		public String toString() {
			return "AparcamientoResumen [nombre=" + nombre + "]";
		}

	}

	private LinkedList<AparcamientoResumen> resumenAparcamientos = new LinkedList<>();

	public LinkedList<AparcamientoResumen> getResumenAparcamientos() {
		return resumenAparcamientos;
	}

	public void setResumenAparcamientos(LinkedList<AparcamientoResumen> resumenAparcamientos) {
		this.resumenAparcamientos = resumenAparcamientos;
	}

	@Override
	public String toString() {
		return "ListadoAparcamiento [resumenAparcamientos=" + resumenAparcamientos + "]";
	}

}
