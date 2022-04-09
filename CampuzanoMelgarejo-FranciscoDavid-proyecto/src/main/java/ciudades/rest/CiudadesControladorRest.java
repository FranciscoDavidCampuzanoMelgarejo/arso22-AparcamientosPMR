package ciudades.rest;

import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.example.ciudades.Ciudad;

public class CiudadesControladorRest {
	private IServicioCiudades servicio = ServicioCiudades.getServicio();

	@Context
	private UriInfo uriInfo;

	/*
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Ciudad ciudad) throws RepositorioException {

		String id = servicio.create(ciudad);

		URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();

		return Response.created(uri).build();
	}
	*/

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") String id) throws RepositorioException, EntidadNoEncontrada {

		Ciudad ciudad = servicio.getCiudad(id);

		return Response.status(Status.OK).entity(ciudad).build();
	}

	@DELETE
	@Path("{id}")
	public Response remove(String id) throws RepositorioException, EntidadNoEncontrada {
		
		servicio.removeCiudad(id);
		
		return Response.noContent().build();
	}
	
	/*
	 * @GET
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response getListadoResumen()
	 * throws Exception {
	 * 
	 * ListadoEncuestas listado = servicio.getListadoResumen();
	 * 
	 * LinkedList<ResumenExtendido> resumenesExtendidos = new LinkedList<>();
	 * 
	 * for (EncuestaResumen resumen : listado.getEncuestas()) {
	 * 
	 * ResumenExtendido resumenExtendido = new ResumenExtendido();
	 * resumenExtendido.setResumen(resumen);
	 * 
	 * String url =
	 * uriInfo.getAbsolutePathBuilder().path(resumen.getId()).build().toString();
	 * 
	 * resumenExtendido.setUrl(url);
	 * 
	 * resumenesExtendidos.add(resumenExtendido); }
	 * 
	 * Listado resultado = new Listado();
	 * resultado.setEncuestas(resumenesExtendidos);
	 * 
	 * return Response.ok(resultado).build(); }
	 */
}

