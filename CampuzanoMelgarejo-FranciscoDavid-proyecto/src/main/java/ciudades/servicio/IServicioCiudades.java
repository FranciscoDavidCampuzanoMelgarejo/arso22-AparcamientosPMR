package ciudades.servicio;

import org.example.ciudades.Ciudad;

import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public interface IServicioCiudades {
	
	
	//Crear una ciudad en el repositorio
	String create(Ciudad ciudad) throws RepositorioException;
	
	//Actualizar una ciudad ya existente en el repositorio
	void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada;
	
	//Recupera una ciudad a partir del identificador
	Ciudad getCiudad(String id) throws RepositorioException, EntidadNoEncontrada;
	
	//Elimina la ciudad a partir de su identificador
	void removeCiudad(String id) throws RepositorioException, EntidadNoEncontrada;
	
	
	
	
	

}
