package ciudades.servicio;

import java.util.List;

import org.example.ciudades.Ciudad;
import org.xml.sax.SAXParseException;

import ciudades.repositorio.FactoriaRepositorioCiudades;
import ciudades.repositorio.RepositorioCiudades;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public class ServicioCiudades implements IServicioCiudades {

	// Obtenemos el repositorio (singleton)
	RepositorioCiudades repositorio = FactoriaRepositorioCiudades.getRepositorio();

	// Patron Singleton para crear el servicio
	private static ServicioCiudades servicio;

	public static ServicioCiudades getServicio() {
		if (servicio == null) {
			servicio = new ServicioCiudades();
		}
		return servicio;
	}

	// Metodo para validar una Ciudad
	protected void validar(Ciudad ciudad) throws IllegalArgumentException {
		List<SAXParseException> validaciones = ValidadorCiudades.validar(ciudad);

		// La ciudad no cumple con el formato del XML Schema
		if (!validaciones.isEmpty()) {
			throw new IllegalArgumentException("La ciudad no cumple el esquema: " + validaciones.get(0).getMessage());
		}
	}

	@Override
	public String create(Ciudad ciudad) throws RepositorioException {

		// Según el esquema, el id es obligatorio
		// Se establece uno provisional, el repositorio aportará el definitivo
		ciudad.setId(" ");

		// TO DO -> la funcion validar lanza la excepcion IllegalArgumentException
		validar(ciudad);

		// Creamos una entrada en el repositorio con la ciudad
		return repositorio.add(ciudad);
	}

	@Override
	public void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada {

		// TO DO -> la funcion validar lanza la excepcion IllegalArgumentException
		validar(ciudad);
		repositorio.update(ciudad);
	}
	
	@Override
	public Ciudad getCiudad(String id) throws RepositorioException, EntidadNoEncontrada {
		
		return repositorio.getById(id);
	}
	
	@Override
	public void removeCiudad(String id) throws RepositorioException, EntidadNoEncontrada {
		
		Ciudad ciudad = repositorio.getById(id);
		repositorio.delete(ciudad);
	}
	
	

}
