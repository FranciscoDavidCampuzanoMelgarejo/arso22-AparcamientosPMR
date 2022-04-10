package ciudades.servicio;


import org.example.ciudades.Aparcamiento;
import org.example.ciudades.Ciudad;

import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public interface IServicioCiudades {

	// Crear una ciudad en el repositorio
	String create(Ciudad ciudad) throws RepositorioException;

	// Actualizar una ciudad ya existente en el repositorio
	void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada;

	// Recupera una ciudad a partir del identificador
	Ciudad getCiudad(String id) throws RepositorioException, EntidadNoEncontrada;

	// Elimina la ciudad a partir de su identificador
	void removeCiudad(String id) throws RepositorioException, EntidadNoEncontrada;

	/* Metodos a realizar */

	// Obtiene un resumen de todas las ciudades del repositorio
	ListadoCiudades getResumenCiudades() throws RepositorioException;

	// Obtiene un resumen de los sitios turisticos de una ciudad
	ListadoSitioTuristico getResumenSitiosTuristicos(String id) throws RepositorioException, EntidadNoEncontrada;

	// Obtener un resumen de las plazas de aparcamiento mas cercanas a un sitio
	// turistico
	ListadoAparcamiento getAparcamientosCercanos(String nombreSitio);

	// Obtener informacion de un aparcamiento
	Aparcamiento getInformacion(String idCiudad, String idAparcamiento)
			throws EntidadNoEncontrada, RepositorioException;

}
