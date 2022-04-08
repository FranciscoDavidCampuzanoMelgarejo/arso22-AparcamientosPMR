package ciudades.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ciudades.repositorio.CiudadNoEncontrada;

@Provider
public class RecuperacionErroresCiudadNoEncontrada implements ExceptionMapper<CiudadNoEncontrada>{
	@Override
	public Response toResponse(CiudadNoEncontrada arg0) {
		return Response.status(Response.Status.NOT_FOUND).entity(arg0.getMessage()).build();
	}
}
