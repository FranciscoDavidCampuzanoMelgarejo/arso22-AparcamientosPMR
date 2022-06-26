package ciudades.rest;

import ciudades.rest.ListadoCiudad.ResumenExtendidoCiudad;
import ciudades.rest.ListadoDistanciaParkingSitio.DistanciaParkingSitioResumen;
import ciudades.rest.ListadoSitioTuristico.SitioTuristicoResumen;
import ciudades.rest.ParkingResumen.OpinionResumen;
import ciudades.servicio.CiudadResumen;
import ciudades.servicio.DistanciaParkingSitio;
import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;
import es.um.ciudades_atom.Autor;
import es.um.ciudades_atom.Entrada;
import es.um.ciudades_atom.Feed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import es.um.ciudades.Ciudad;
import es.um.ciudades.Parking;
import es.um.ciudades.SitioTuristico;
import org.joda.time.LocalDateTime;

@Api
@Path("ciudades")
public class CiudadesControladorRest {
	private IServicioCiudades servicio = ServicioCiudades.getServicio();

	@Context
	private UriInfo uriInfo;

	// curl -i -X GET -H "Accept: application/xml
	// http://localhost:8081/api/ciudades/Lorca"
	@GET
	@Path("/{nombre}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consulta de una ciudad", notes = "Devuelve la ciudad consultada a traves de su nombre", response = Ciudad.class)
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al carga la ciudad"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad no encontrada") })
	public Response getCiudad(
			@ApiParam(value = "Nombre de la ciudad", required = true) @PathParam("nombre") String nombre)
			throws Exception {
		return Response.status(Response.Status.OK).entity(servicio.getCiudad(nombre)).build();
	}

	// String create(Ciudad ciudad) throws RepositorioException

	// curl -i -X POST -H "Accept: application/xml" -d @ruta_fichero/fichero.xml
	// http://localhost:8081/api/ciudades
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Crear una nueva ciudad", notes = "No devuelve nada")
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_CREATED, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al guardar la ciudad") })
	public Response create(Ciudad ciudad) throws Exception {
		String nombre = servicio.create(ciudad);
		URI uri = uriInfo.getAbsolutePathBuilder().path(nombre).build();
		return Response.created(uri).build();
	}

	// void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada

	// curl -i -X PUT -H "Content-type: application/xml" -d @test-files/fichero.xml
	// http://localhost:8081/api/ciudades/Lorca

	@PUT
	@Path("/{nombre}")
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(value = "Actualizar una ciudad", notes = "No devuelve nada")
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al guardar la ciudad"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad no encontrada") })
	public Response update(@ApiParam(value = "Nombre de la ciudad", required = true) @PathParam("nombre") String nombre,
			Ciudad ciudad) throws Exception {

		servicio.update(ciudad);
		return Response.status(Status.NO_CONTENT).build();
	}

	// void delete(String id) throws RepositorioException, EntidadNoEncontrada

	// curl -i -X DELETE
	// http://localhost:8081/api/ciudades/Lorca
	@DELETE
	@Path("/{nombre}")
	@ApiOperation(value = "Eliminar una ciudad", notes = "No devuelve nada")
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al cargar la ciudad"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad no encontrada") })
	public Response remove(@ApiParam(value = "Nombre de la ciudad", required = true) @PathParam("nombre") String nombre)
			throws Exception {

		servicio.removeCiudad(nombre);

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	// Metodo para obtener un resumen de cada ciudad disponible
	// curl -i http://localhost:8081/api/ciudades

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consulta las ciudades disponibles", notes = "Devuelve un listado de las ciudades", response = ListadoCiudad.class)
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al cargar las ciudades") })
	public Response getCiudades() throws Exception {
		List<CiudadResumen> ciudadesResumen = servicio.getCiudades();

		LinkedList<ResumenExtendidoCiudad> resumenes = new LinkedList<>();

		for (CiudadResumen ciudad : ciudadesResumen) {
			ResumenExtendidoCiudad resumenExtendido = new ResumenExtendidoCiudad();
			resumenExtendido.setCiudadResumen(ciudad);

			URI url = uriInfo.getAbsolutePathBuilder().path(ciudad.getNombre()).path("sitios").build();
			resumenExtendido.setUrl(url.toString());
			resumenes.add(resumenExtendido);
		}

		ListadoCiudad listado = new ListadoCiudad();
		listado.setCiudadesResumen(resumenes);

		return Response.status(Response.Status.OK).entity(listado).build();
	}

	// curl -i http://localhost:8081/api/ciudades/atom

	@GET
	@Path("/atom")
	@Produces({ MediaType.APPLICATION_ATOM_XML })
	public Response getCiudadesAtom() throws Exception {
		Feed feed = new Feed();
		feed.setId(uriInfo.getBaseUri().toString());
		feed.setTitle("Servicio ciudades");
		feed.setSubtitle("Sitios turisticos y aparcamientos para personas con movilidad reducida en varias ciudades");

		File ficheroAtom = new File("/xml/ciudades-atom.xsd");
		long fechaModificacion = ficheroAtom.lastModified();
		feed.setUpdated(new LocalDateTime(fechaModificacion).toString());

		Feed.Link enlace = new Feed.Link();
		enlace.setHref(uriInfo.getAbsolutePath().toString());

		Autor autor1 = new Autor();
		autor1.setName("Francisco David Campuzano Melgarejo");
		autor1.setEmail("franciscodavid.campuzanom@um.es");
		feed.getAuthor().add(autor1);

		List<CiudadResumen> ciudadesResumen = servicio.getCiudades();
		for (CiudadResumen ciudad : ciudadesResumen) {
			Entrada entrada = new Entrada();

			URI url = uriInfo.getBaseUriBuilder().path("ciudades").path(ciudad.getNombre()).build();
			entrada.setId(url.toString());
			entrada.setTitle(ciudad.getNombre());

			File ficheroCiudad = new File("/ciudades/" + ciudad.getNombre() + ".xml");
			fechaModificacion = ficheroCiudad.lastModified();
			entrada.setUpdated(new LocalDateTime(fechaModificacion).toString());
			feed.getEntry().add(entrada);
		}

		return Response.status(Response.Status.OK).entity(feed).build();

	}

	// Metodo para obtener todos los sitios turisticos de una determinada ciudad
	// curl -i http://localhost:8081/api/ciudades/Lorca/sitios

	@GET
	@Path("/{nombre}/sitios")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consulata los sitios turisticos de una ciudad", notes = "Devuelve un listado de los sitios turisticos utilizando el nombre de una ciudad", response = ListadoSitioTuristico.class)
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al cargar los sitios turisticos"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad no encontrada") })
	public Response getSitiosTuristicos(
			@ApiParam(value = "nombre de la ciudad", required = true) @PathParam("nombre") String nombre)
			throws Exception {
		List<SitioTuristico> sitiosTuristicos = servicio.getSitiosTuristicos(nombre);

		LinkedList<SitioTuristicoResumen> resumenes = new LinkedList<>();

		for (SitioTuristico st : sitiosTuristicos) {
			SitioTuristicoResumen resumen = new SitioTuristicoResumen();
			resumen.setTitulo(st.getTitulo());
			resumen.setResumen(st.getResumen());
			resumen.setLatitud(st.getLatitud());
			resumen.setLongitud(st.getLongitud());
			resumen.setWikipedia(st.getWikipedia());

			URI url = uriInfo.getAbsolutePathBuilder().path(st.getTitulo()).path("parkings").queryParam("radio", 10)
					.build();
			resumen.setUrl(url.toString());

			resumenes.add(resumen);
		}

		ListadoSitioTuristico listado = new ListadoSitioTuristico();
		listado.setSitiosResumen(resumenes);

		return Response.status(Response.Status.OK).entity(listado).build();
	}

	// Metodo para obtener los aparcamientos cercanos a un sitio determinado de una
	// ciudad
	// curl -i
	// http://localhost:8081/api/ciudades/Lorca/sitios/Castillo_de_Lorca/parkings

	@GET
	@Path("/{nombre}/sitios/{sitio}/parkings")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consulta los parkings cercanos a un sitio turistico", notes = "Devuelve un listado de parkings cercanos a un sitio turistio utilizando el nombre de la ciudad y el del sitio turistico", response = ListadoDistanciaParkingSitio.class)
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al cargar los parkings"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad o sitio turistico no encontrados") })
	@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "El radio introducio no esta en el rango permitido")
	public Response getParkingsCercanos(
			@ApiParam(value = "Nombre de la ciudad", required = true) @PathParam("nombre") String nombre,
			@ApiParam(value = "Nombre del sitio turistico", required = true) @PathParam("sitio") String sitio,
			@ApiParam(value = "Radio de la busqueda de parkings cercanos", required = false) @QueryParam("radio") Double radio)
			throws Exception {
		List<DistanciaParkingSitio> parkings = servicio.getAparcamientosCercanos(nombre, sitio, radio);

		LinkedList<DistanciaParkingSitioResumen> resumenes = new LinkedList<>();

		for (DistanciaParkingSitio p : parkings) {
			DistanciaParkingSitioResumen resumen = new DistanciaParkingSitioResumen();
			resumen.setDistancia(p.getDistancia());
			resumen.setLatitud(p.getParking().getLatitud());
			resumen.setLongitud(p.getParking().getLongitud());

			URI url = uriInfo.getBaseUriBuilder().path("ciudades").path(nombre).path("parkings")
					.path(String.valueOf(p.getParking().getLatitud()))
					.path(String.valueOf(p.getParking().getLongitud())).build();

			resumen.setUrl(url.toString());
			resumenes.add(resumen);
		}

		ListadoDistanciaParkingSitio listado = new ListadoDistanciaParkingSitio();
		listado.setParkings(resumenes);

		return Response.status(Response.Status.OK).entity(listado).build();
	}

	// curl -i
	// http://localhost:8081/api/ciudades/Lorca/parkings/37.65498859455905/-1.7114953133709605

	@GET
	@Path("/{nombre}/parkings/{lat}/{lng}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Consulta un parking", notes = "Devuelve un parking utilizando el nombre de la ciudad y las coordenadas geograficas", response = ParkingResumen.class)
	@ApiResponses(value = { @ApiResponse(code = HttpServletResponse.SC_OK, message = ""),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Error al carga la ciudad"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Ciudad no encontrada") })
	public Response getParking(
			@ApiParam(value = "Nombre de la ciudad", required = true) @PathParam("nombre") String nombre,
			@ApiParam(value = "Latitud del parking", required = true) @PathParam("lat") double lat,
			@ApiParam(value = "Longitud del parking", required = true) @PathParam("lng") double lng) throws Exception {
		Parking parking = servicio.getParking(nombre, lat, lng);

		ParkingResumen resumen = new ParkingResumen();

		resumen.setDireccion(parking.getDireccion());
		resumen.setLatitud(parking.getLatitud());
		resumen.setLongitud(parking.getLongitud());

		if (parking.getOpinion() != null) {
			OpinionResumen opinionResumen = new OpinionResumen();
			opinionResumen.setUrlOpinion(parking.getOpinion().getUrlOpinion());
			opinionResumen.setValoraciones(parking.getOpinion().getNumeroValoraciones());
			opinionResumen.setCalificacionMedia(parking.getOpinion().getCalificacionMedia());
			resumen.setOpinion(opinionResumen);
		}

		return Response.status(Response.Status.OK).entity(resumen).build();
	}

}
