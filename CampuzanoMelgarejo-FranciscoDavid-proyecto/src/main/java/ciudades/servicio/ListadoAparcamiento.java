package ciudades.servicio;

import java.util.LinkedList;

public class ListadoAparcamiento {

	public static class AparcamientoResumen {

		private String id;
		private String direccion;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDireccion() {
			return direccion;
		}

		public void setDireccion(String direccion) {
			this.direccion = direccion;
		}

		@Override
		public String toString() {
			return "AparcamientoResumen [id=" + id + ", direccion=" + direccion + "]";
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
