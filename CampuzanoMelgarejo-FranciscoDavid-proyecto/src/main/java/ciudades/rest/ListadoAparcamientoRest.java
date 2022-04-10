package ciudades.rest;

import java.util.LinkedList;

import ciudades.servicio.ListadoAparcamiento.AparcamientoResumen;

public class ListadoAparcamientoRest {

	public static class AparcamientoResumenExtendido {

		private String url;
		private AparcamientoResumen resumen;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public AparcamientoResumen getResumen() {
			return resumen;
		}

		public void setResumen(AparcamientoResumen resumen) {
			this.resumen = resumen;
		}

	}

	private LinkedList<AparcamientoResumenExtendido> resumen = new LinkedList<>();

	public LinkedList<AparcamientoResumenExtendido> getResumen() {
		return resumen;
	}

	public void setResumen(LinkedList<AparcamientoResumenExtendido> resumen) {
		this.resumen = resumen;
	}

}
