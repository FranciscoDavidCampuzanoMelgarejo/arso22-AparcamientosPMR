package ciudades.rest;

import java.util.LinkedList;

import ciudades.servicio.ListadoAparcamiento.AparcamientoResumen;
import ciudades.servicio.ListadoCiudades.CiudadResumen;

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

		public CiudadResumen getResumen() {
			return resumen;
		}

		public void setResumen(CiudadResumen resumen) {
			this.resumen = resumen;
		}

	}

	private LinkedList<CiudadResumenExtendido> resumen = new LinkedList<>();

	public LinkedList<CiudadResumenExtendido> getResumen() {
		return resumen;
	}

	public void setResumen(LinkedList<CiudadResumenExtendido> resumen) {
		this.resumen = resumen;
	}

}
