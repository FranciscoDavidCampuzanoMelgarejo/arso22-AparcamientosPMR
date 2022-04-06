package ciudades.repositorio;

import java.util.List;

import org.example.ciudades.Ciudad;
import org.example.ciudades.SitioTuristico;

import repositorio.Repositorio;
import repositorio.RepositorioException;

public interface RepositorioCiudades extends Repositorio<Ciudad, String> {

	/*
	 * El repositorio declara los métodos básicos de gestión de la entidad.
	 * 
	 * En esta interfaz podemos añadir métodos más específicos.
	 */

	// Dada una ciudad devuelve todos los sitios turisticos de ésta
	List<SitioTuristico> getAllSitiosTuristicos(Ciudad ciudad) throws RepositorioException;

}
