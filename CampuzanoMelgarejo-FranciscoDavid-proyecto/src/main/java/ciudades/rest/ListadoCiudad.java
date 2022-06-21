package ciudades.rest;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import ciudades.servicio.CiudadResumen;


@XmlRootElement
public class ListadoCiudad {

	public static class ResumenExtendidoCiudad {

		private CiudadResumen ciudadResumen;
		private String url;

		public CiudadResumen getCiudadResumen() {
			return ciudadResumen;
		}

		public void setCiudadResumen(CiudadResumen ciudadResumen) {
			this.ciudadResumen = ciudadResumen;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	private List<ResumenExtendidoCiudad> ciudadesResumen = new LinkedList<>();

	public List<ResumenExtendidoCiudad> getCiudadesResumen() {
		return ciudadesResumen;
	}

	public void setCiudadesResumen(List<ResumenExtendidoCiudad> ciudadesResumen) {
		this.ciudadesResumen = ciudadesResumen;
	}
}
