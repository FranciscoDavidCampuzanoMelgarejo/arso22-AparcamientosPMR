package ciudades.servlet;

import java.io.Serializable;

import javax.servlet.http.HttpServlet;

import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;

public class RegistroCola extends HttpServlet implements Serializable {

	private IServicioCiudades servicio = null;

	public RegistroCola() {
		servicio = ServicioCiudades.getServicio();
	}

}
