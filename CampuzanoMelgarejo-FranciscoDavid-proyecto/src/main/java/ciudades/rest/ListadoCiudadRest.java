package ciudades.rest;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlRootElement;

import ciudades.servicio.ListadoCiudades.CiudadResumen;

@XmlRootElement
public class ListadoCiudadRest {

	public static class CiudadResumenExtendido {

		private String url;
		private CiudadResumen resumen;

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
