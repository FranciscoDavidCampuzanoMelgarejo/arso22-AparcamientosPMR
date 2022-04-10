package ciudades.servicio;

import java.util.LinkedList;

public class ListadoAparcamiento {

	public static class AparcamientoResumen {

		private String id;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "AparcamientoResumen [id=" + id + "]";
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
