package ciudades.repositorio;

/*
 * Excepción notificada si no existe el identificador de la entidad.
 */

@SuppressWarnings("serial")
public class CiudadNoEncontrada extends Exception {

	public CiudadNoEncontrada(String msg, Throwable causa) {
		super(msg, causa);
	}

	public CiudadNoEncontrada(String msg) {
		super(msg);
	}

}
