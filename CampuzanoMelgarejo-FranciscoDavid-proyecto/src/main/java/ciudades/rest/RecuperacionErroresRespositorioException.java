package ciudades.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ciudades.repositorio.RepositorioException;

@Provider
public class RecuperacionErroresRespositorioException implements ExceptionMapper<RepositorioException>{
	@Override
	public Response toResponse(RepositorioException arg0) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(arg0.getMessage()).build();
	}
}
