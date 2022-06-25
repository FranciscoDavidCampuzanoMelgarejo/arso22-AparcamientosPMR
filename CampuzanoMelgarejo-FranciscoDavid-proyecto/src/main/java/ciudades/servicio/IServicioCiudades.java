package ciudades.servicio;

import java.util.List;
import es.um.ciudades.Ciudad;
import es.um.ciudades.Parking;
import es.um.ciudades.SitioTuristico;

import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public interface IServicioCiudades {

	// CRUD

	String create(Ciudad ciudad) throws RepositorioException;

	void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada;

	Ciudad getCiudad(String nombre) throws RepositorioException, EntidadNoEncontrada;

	void removeCiudad(String nombre) throws RepositorioException, EntidadNoEncontrada;

	/* Metodos a realizar */

	// Devuelve todas las ciudades
	List<CiudadResumen> getCiudades() throws RepositorioException;

	// Devuelve todos los sitios turisticos de una ciudad
	List<SitioTuristico> getSitiosTuristicos(String nombre) throws RepositorioException, EntidadNoEncontrada;

	// Devuelve todos los aparcamientos cercanos a un sitio turistico de una ciudad.
	// Se le puede indicar el radio (km)
	List<DistanciaParkingSitio> getAparcamientosCercanos(String nombre, String sitio, Double radio)
			throws RepositorioException, EntidadNoEncontrada;

	// Devuelve un parking de una ciudad
	Parking getParking(String nombre, double lat, double lng) throws RepositorioException, EntidadNoEncontrada;

}
