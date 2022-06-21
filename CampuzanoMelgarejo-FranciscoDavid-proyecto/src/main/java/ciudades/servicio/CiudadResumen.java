package ciudades.servicio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ciudadResumen", propOrder = {
	    "nombre",
	    "sitios",
	    "aparcamientos"
	})
public class CiudadResumen {

	private String nombre;
	private int sitios;
	private int aparcamientos;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getSitios() {
		return sitios;
	}

	public void setSitios(int sitios) {
		this.sitios = sitios;
	}

	public int getAparcamientos() {
		return aparcamientos;
	}

	public void setAparcamientos(int aparcamientos) {
		this.aparcamientos = aparcamientos;
	}

	@Override
	public String toString() {
		return "CiudadResumen [nombre=" + nombre + ", sitios=" + sitios + ", aparcamientos=" + aparcamientos + "]";
	}

}
