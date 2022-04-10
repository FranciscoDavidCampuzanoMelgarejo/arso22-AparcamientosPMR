package ciudades.rest;

import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.example.ciudades.Aparcamiento;
import org.example.ciudades.Ciudad;

@Path("ciudades")
public class CiudadesControladorRest {
	private IServicioCiudades servicio = ServicioCiudades.getServicio();

	@Context
	private UriInfo uriInfo;

	// Ciudad getCiudad(String id) throws RepositorioException, EntidadNoEncontrada

	// curl -i -X GET -H "Accept: application/xml"
	// http://localhost:8080/api/ciudades/1
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getCiudad(@PathParam("id") String id) throws Exception {
		return Response.status(Response.Status.OK).entity(servicio.getCiudad(id)).build();
	}

	// String create(Ciudad ciudad) throws RepositorioException

	// curl -i -X POST -H "Accept: application/xml" -d @ruta_fichero/fichero.xml
	// http://localhost:8080/api/ciudades
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response create(Ciudad ciudad) throws Exception {
		String id = servicio.create(ciudad);
		URI uri = uriInfo.getAbsolutePathBuilder().path(id).build();
		return Response.created(uri).build();
	}

	// void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada

	// curl -i -X PUT
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response update(@PathParam("id") String id, Ciudad ciudad) throws Exception {
		if (!id.equals(ciudad.getId())) {
			throw new IllegalArgumentException("El identificador: '" + id + "' no coincide con el de la ciudad");
		}

		servicio.update(ciudad);
		return Response.status(Status.NO_CONTENT).build();
	}

	// void delete(String id) throws RepositorioException, EntidadNoEncontrada

	// curl -i -X DELETE
	@DELETE
	@Path("{id}")
	public Response remove(String id) throws Exception {

		servicio.removeCiudad(id);

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	//Aparcamiento getInformacion(String id, String idAparcamiento)
	
	//curl -i -X GET
	@GET
	@Path("/{id}/{idAparcamiento}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getInformacion(@PathParam("id") String id, @PathParam("idAparcamiento") String idAparcamiento)
			throws Exception {
		
		System.out.println("VA");
		Aparcamiento a = servicio.getInformacion(id, idAparcamiento);
		System.out.println(a.getId());
		return Response.status(Response.Status.OK).entity(a).build();

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
