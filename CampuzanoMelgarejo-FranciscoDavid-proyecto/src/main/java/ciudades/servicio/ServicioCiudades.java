package ciudades.servicio;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.json.bind.Jsonb;
import javax.json.bind.spi.JsonbProvider;

import es.um.ciudades.Ciudad;
import es.um.ciudades.Opinion;
import es.um.ciudades.Parking;
import es.um.ciudades.SitioTuristico;
import org.xml.sax.SAXParseException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;

import ciudades.repositorio.FactoriaRepositorioCiudades;
import ciudades.repositorio.RepositorioCiudades;
import opiniones.eventos.EventoValoracionCreada;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;
import utils.Utils;

public class ServicioCiudades implements IServicioCiudades {

	private static final double RADIO_POR_DEFECTO = 10; // 10 km de radio

	// Obtenemos el repositorio (singleton)
	RepositorioCiudades repositorio = FactoriaRepositorioCiudades.getRepositorio();

	private Map<String, List<DistanciaParkingSitio>> parkingsCercanos = new HashMap<>();

	// Patron Singleton para crear el servicio
	private static ServicioCiudades servicio;

	public static ServicioCiudades getServicio() {
		if (servicio == null) {
			servicio = new ServicioCiudades();
		}
		return servicio;
	}

	private ServicioCiudades() {

		try {

			ConnectionFactory factoria = new ConnectionFactory();
			factoria.setUri("amqps://jsbkllto:1u9IreNKv84X7571xO4Rx1jiGQmlcymS@stingray.rmq.cloudamqp.com/jsbkllto");

			Connection conexion = factoria.newConnection();
			Channel canal = conexion.createChannel();

			// Declaracion de la cola y enlazarla con el exchange
			final String nombreExchange = "opiniones-exchange";
			final String nombreCola = "opiniones-queue";
			final String bindingkey = "arso";

			boolean durable = true;
			boolean exclusive = false;
			boolean autodelete = false;
			Map<String, Object> propiedades = null; // sin propiedades

			System.out.println("Declararla cola");
			canal.queueDeclare(nombreCola, durable, exclusive, autodelete, propiedades);

			System.out.println("Atar la cola");
			canal.queueBind(nombreCola, nombreExchange, bindingkey);
			System.out.println("Cola atada");
			// Configuracion del consumidor

			boolean autoAck = false;
			String cola = "opiniones-queue";
			String etiquetaConsumidor = "arso-consumidor";

			// Consumidor push

			System.out.println("Antes de consumir");
			canal.basicConsume(cola, autoAck, etiquetaConsumidor, new DefaultConsumer(canal) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					System.out.println("CONSUMIENDO");
					String routingKey = envelope.getRoutingKey();
					String contentType = properties.getContentType();
					long deliveryTag = envelope.getDeliveryTag();

					String contenido = new String(body);
					System.out.println(contenido);

					Jsonb contexto = JsonbProvider.provider().create().build();

					EventoValoracionCreada evento = contexto.fromJson(contenido, EventoValoracionCreada.class);

					String url = evento.getUrl();
					String nombreCiudad = null;
					double lat, lng;
					lat = lng = 0.0;

					// TO DO -> Obtener la ciudad de la URL
					Pattern patron_ciudad = Pattern.compile("ciudades[/][A-Z][a-z]*[/]");
					Pattern patron_parking = Pattern.compile("([-]?[0-9]+[.][0-9]+)");
					Matcher match_ciudad = patron_ciudad.matcher(url);
					Matcher match_parking = patron_parking.matcher(url);

					if (match_ciudad.find())
						nombreCiudad = match_ciudad.group().substring(match_ciudad.group().indexOf('/') + 1,
								match_ciudad.group().lastIndexOf('/'));

					double[] coordenadas = new double[2];
					int i = 0;
					while (match_parking.find())
						coordenadas[i++] = Double.parseDouble(match_parking.group());

					lat = coordenadas[0];
					lng = coordenadas[1];

					try {
						Ciudad ciudad = repositorio.getByNombre(nombreCiudad);
						for (Parking p : ciudad.getParking()) {
							if (p.getLatitud() == lat && p.getLongitud() == lng) {

								Opinion opinion = new Opinion();
								opinion.setUrlOpinion("http://localhost:8081/api/opiniones/" + url);
								opinion.setCalificacionMedia(evento.getCalificacionMedia());
								opinion.setNumeroValoraciones(evento.getValoraciones());
								p.setOpinion(opinion);
								ciudad.getParking().add(p);
								break;
							}
						}

						repositorio.update(ciudad);
					} catch (RepositorioException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (EntidadNoEncontrada e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Confirma el procesamiento
					canal.basicAck(deliveryTag, false);
				}
			});

			System.out.println("Final consumidor");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Metodo para calcular los parkings mas cercanos a un sitio
	private void calcularAparcamientosCercanos(Ciudad ciudad, SitioTuristico sitio) {

		// Array que guarda las distancias (km) entre un sitio y un parking
		double[] distancias = new double[ciudad.getParking().size()];
		for (int i = 0; i < ciudad.getParking().size(); i++) {
			Parking parking = ciudad.getParking().get(i);
			distancias[i] = Utils.calcularDistancia(sitio.getLatitud(), sitio.getLongitud(), parking.getLatitud(),
					parking.getLongitud());
		}

		// Array que contiene los indices de los parkings
		int[] indicesParkings = IntStream.range(0, ciudad.getParking().size()).toArray();
		Utils.ordenarParkings(distancias, indicesParkings, 0, ciudad.getParking().size() - 1);

		List<DistanciaParkingSitio> parkingsDistancia = new LinkedList<>();
		for (int i = 0; i < ciudad.getParking().size(); i++) {
			Parking p = ciudad.getParking().get(indicesParkings[i]);
			parkingsDistancia.add(new DistanciaParkingSitio(p, distancias[i]));
		}

		parkingsCercanos.put(sitio.getTitulo(), parkingsDistancia);

	}

	// Metodo para validar una Ciudad
	protected void validar(Ciudad ciudad) throws IllegalArgumentException {
		List<SAXParseException> validaciones = ValidadorCiudades.validar(ciudad);

		// La ciudad no cumple con el formato del XML Schema
		if (!validaciones.isEmpty()) {
			throw new IllegalArgumentException("La ciudad no cumple el esquema: " + validaciones.get(0).getMessage());
		}
	}

	@Override
	public String create(Ciudad ciudad) throws RepositorioException {

		try {
			validar(ciudad);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// Creamos una entrada en el repositorio con la ciudad
		String nombre = repositorio.add(ciudad);
		return nombre;
	}

	@Override
	public void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada {

		try {
			validar(ciudad);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		repositorio.update(ciudad);
	}

	@Override
	public Ciudad getCiudad(String nombre) throws RepositorioException, EntidadNoEncontrada {

		return repositorio.getByNombre(nombre);
	}

	@Override
	public void removeCiudad(String nombre) throws RepositorioException, EntidadNoEncontrada {

		Ciudad ciudad = repositorio.getByNombre(nombre);
		repositorio.delete(ciudad);
	}

	@Override
	public List<CiudadResumen> getCiudades() throws RepositorioException {
		List<CiudadResumen> ciudades = new LinkedList<>();

		for (Ciudad ciudad : repositorio.getAll()) {
			CiudadResumen resumen = new CiudadResumen();
			resumen.setNombre(ciudad.getNombre());
			resumen.setSitios(ciudad.getSitioTuristico().size());
			resumen.setAparcamientos(ciudad.getParking().size());
			ciudades.add(resumen);
		}
		return ciudades;
	}

	@Override
	public List<SitioTuristico> getSitiosTuristicos(String nombre) throws RepositorioException, EntidadNoEncontrada {
		Ciudad ciudad = repositorio.getByNombre(nombre);
		return ciudad.getSitioTuristico();
	}

	@Override
	public List<DistanciaParkingSitio> getAparcamientosCercanos(String nombre, String sitio, Double radio)
			throws RepositorioException, EntidadNoEncontrada {

		if (radio == null)
			radio = RADIO_POR_DEFECTO;
		else if (radio <= 0 || radio > 50) {
			throw new IllegalArgumentException("El radio debe estar en el rango (0-50]");
		}

		Ciudad ciudad = repositorio.getByNombre(nombre);
		int i = 0;
		while ((i < ciudad.getSitioTuristico().size())
				&& (!ciudad.getSitioTuristico().get(i).getTitulo().equals(sitio)))
			i++;

		if (i >= ciudad.getSitioTuristico().size()) {
			throw new EntidadNoEncontrada("El sitio turistico: " + sitio + "no esta registrado");
		}

		if (!parkingsCercanos.containsKey(sitio)) {
			System.out.println("DENTRO");
			calcularAparcamientosCercanos(ciudad, ciudad.getSitioTuristico().get(i));
		}

		List<DistanciaParkingSitio> parkingsDistancias = new LinkedList<>();
		List<DistanciaParkingSitio> distanciasCalculadas = parkingsCercanos.get(sitio);

		i = 0;
		while (i < ciudad.getParking().size() && distanciasCalculadas.get(i).getDistancia() <= radio) {
			parkingsDistancias.add(distanciasCalculadas.get(i));
			i++;
		}
		return parkingsDistancias;
	}

	@Override
	public Parking getParking(String nombre, double lat, double lng) throws RepositorioException, EntidadNoEncontrada {
		Ciudad ciudad = repositorio.getByNombre(nombre);
		for (Parking p : ciudad.getParking()) {
			if (p.getLatitud() == lat && p.getLongitud() == lng) {
				return p;
			}
		}
		throw new EntidadNoEncontrada("No existe ningun aparcamiento en esas coordenadas");
	}

}
