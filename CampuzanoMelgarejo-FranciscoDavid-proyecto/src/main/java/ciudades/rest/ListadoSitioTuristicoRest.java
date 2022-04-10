package ciudades.rest;

import java.util.LinkedList;

import ciudades.servicio.ListadoSitioTuristico.SitioTuristicoResumen;

public class ListadoSitioTuristicoRest {

	public static class SitioTuristicoResumenExtendido {

		private String url;
		private SitioTuristicoResumen resumen;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public SitioTuristicoResumen getResumen() {
			return resumen;
		}

		public void setResumen(SitioTuristicoResumen resumen) {
			this.resumen = resumen;
		}

	}

	private LinkedList<SitioTuristicoResumenExtendido> resumen = new LinkedList<>();

	public LinkedList<SitioTuristicoResumenExtendido> getResumen() {
		return resumen;
	}

	public void setResumen(LinkedList<SitioTuristicoResumenExtendido> resumen) {
		this.resumen = resumen;
	}

}
