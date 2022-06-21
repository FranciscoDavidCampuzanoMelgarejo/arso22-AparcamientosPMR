package ciudades.servlet;

import javax.servlet.http.HttpServlet;

import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;

public class RegistroCola extends HttpServlet {
	
	private IServicioCiudades servicio = null;
	
	public RegistroCola() {
		System.out.println("Servlet creado");
		servicio = ServicioCiudades.getServicio();
	}

}
