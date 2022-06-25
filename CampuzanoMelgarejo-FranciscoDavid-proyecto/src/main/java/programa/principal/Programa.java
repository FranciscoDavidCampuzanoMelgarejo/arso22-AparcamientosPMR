package programa.principal;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import es.um.ciudades.Ciudad;
import es.um.ciudades.Parking;
import es.um.ciudades.SitioTuristico;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ciudades.repositorio.FactoriaRepositorioCiudades;
import ciudades.repositorio.RepositorioCiudades;
import ciudades.servicio.CiudadResumen;
import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;
import sax.Aparcamiento;
import sax.Manejador;

public class Programa {

	private static final String LORCA = "Lorca";
	private static final String MALAGA = "Malaga";

	private static boolean esUnSitioTuristico(JsonObject objetoResource) {
		JsonArray arrayCaracteristicas = objetoResource.getJsonArray("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

		String estructuraArqt = "http://dbpedia.org/ontology/ArchitecturalStructure";
		String parque = "http://dbpedia.org/ontology/Park";
		if (arrayCaracteristicas != null) {

			for (int i = 0; i < arrayCaracteristicas.size(); i++) {
				JsonObject objeto = arrayCaracteristicas.getJsonObject(i);

				String valor = objeto.getString("value");
				if (valor.equals(estructuraArqt) || valor.equals(parque)) {
					return true;
				}
			}

		}

		final String figura = "http://es.dbpedia.org/property/figura";
		final String estadio = "http://es.dbpedia.org/property/estadio";
		final String construccion = "http://es.dbpedia.org/property/construcción";

		return (objetoResource.getJsonArray(figura) != null || objetoResource.getJsonArray(estadio) != null
				|| objetoResource.getJsonArray(construccion) != null);

	}

	static Ciudad construirCiudad(String nombreCiudad)
			throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
		List<Parking> parkings = new LinkedList<>();
		String urlPostalCodeSearch = null;

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression consulta = null;

		switch (nombreCiudad) {
		case LORCA:
			final String urlAparcamientosLorca = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
			urlPostalCodeSearch = "http://api.geonames.org/postalCodeSearch?maxRows=500&username=francisco_david&placename=Lorca&country=ES";
			Manejador manejador = new Manejador();
			/* PARTE DE SAX */

			// 1. Contruir el analizador (parser) usando la factoria
			SAXParser analizador = SAXParserFactory.newInstance().newSAXParser();

			// 2. Analizar el documentoSAX
			try {
				analizador.parse(urlAparcamientosLorca, manejador);
			} catch (SAXException e) {
				System.out.println("El procesamiento se ha detenido por errores sintácticos");
			} catch (IOException e) {
				System.out.println("No se ha podido abrir el documentoSAX: " + urlAparcamientosLorca);
			}

			for (Aparcamiento a : manejador.getAparcamientos()) {
				Parking parking = new Parking();
				parking.setDireccion(a.getDireccion());
				parking.setLatitud(a.getLatitud());
				parking.setLongitud(a.getLongitud());
				parkings.add(parking);
			}

			// XPath
			// Obtener todos los elementos <postalcode> en los que su elemento hermano
			// <adminName1> tenga el valor de Murcia
			consulta = xpath.compile("//postalcode[../adminName1/text() = 'Murcia']");
			break;

		case MALAGA:
			urlPostalCodeSearch = "http://api.geonames.org/postalCodeSearch?maxRows=500&username=francisco_david&placename=Malaga&country=ES";
			final String urlAparcamientosMalaga = "https://datosabiertos.malaga.eu/recursos/transporte/trafico/da_aparcamientosMovilidadReducida-4326.geojson";

			// Obtener el array del documento json con la informacion de los aparcamientos
			// en Malaga
			JsonReader lector = Json.createReader(new URL(urlAparcamientosMalaga).openStream());
			JsonObject objetoJson = lector.readObject();
			JsonArray caracteristicas = objetoJson.getJsonArray("features");

			// Obtener los aparcamientos de la ciudad de Malaga
			for (int i = 0; i < caracteristicas.size(); i++) {
				JsonObject objAparcamiento = caracteristicas.getJsonObject(i);
				Parking parking = new Parking();
				parking.setLongitud(Double.parseDouble(
						objAparcamiento.getJsonObject("geometry").getJsonArray("coordinates").get(0).toString()));
				parking.setLatitud(Double.parseDouble(
						objAparcamiento.getJsonObject("geometry").getJsonArray("coordinates").get(1).toString()));
				String descripcion = objAparcamiento.getJsonObject("properties").getJsonString("description")
						.toString();
				parking.setDireccion(descripcion.substring(descripcion.lastIndexOf("Ubicación") + 15,
						descripcion.lastIndexOf("N° Plazas") - 10));
				parkings.add(parking);
			}

			// XPath
			// Obtener todos los elementos <postalcode> en los que su elemento hermano
			// <adminName1> tenga el valor de Murcia
			consulta = xpath.compile("//postalcode[../adminName3/text() = 'Málaga']");
			break;

		default:
			throw new IllegalArgumentException("La ciudad: " + nombreCiudad + "no puede ser procesada");
		}

		/* PARTE DE DOM */

		// Conjunto de CP de una ciudad (no repetidos)
		Set<String> codigosPostales = new HashSet<>();

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document documento = docBuilder.parse(urlPostalCodeSearch);

		// Obtenemos todos los codigos postales
		NodeList nodos = (NodeList) consulta.evaluate(documento, XPathConstants.NODESET);
		for (int i = 0; i < nodos.getLength(); i++) {
			codigosPostales.add(nodos.item(i).getTextContent());
		}

		System.out.println(codigosPostales);

		// Conjunto de sitios (nombre) que tiene una entrada en Wikipedia (no repetidos)
		Set<String> nombresSitios = new HashSet<>();
		List<SitioTuristico> sitiosTuristicos = new LinkedList<>();

		for (String cp : codigosPostales) {
			String sitio = "http://api.geonames.org/findNearbyWikipedia?postalcode=" + cp
					+ "&country=ES&radius=10&username=francisco_david&maxRows=500&lang=ES";
			Document documentoWikipedia = docBuilder.parse(sitio);
			NodeList nodosSitios = documentoWikipedia.getElementsByTagName("entry");

			// De cada entrada, obtenemos la informacion necesaria
			for (int i = 0; i < nodosSitios.getLength(); i++) {
				Element elemento = (Element) nodosSitios.item(i);
				// Nombre del sitio
				String titulo = elemento.getElementsByTagName("title").item(0).getTextContent();

				if (nombresSitios.add(titulo)) {
					String wikipedia = elemento.getElementsByTagName("wikipediaUrl").item(0).getTextContent();
					String entradaWiki = wikipedia.substring(wikipedia.lastIndexOf('/') + 1);
					String urlDbpedia = "https://es.dbpedia.org/data/" + entradaWiki + ".json";

					// DBPEDIA

					JsonReader lector = Json.createReader(new URL(urlDbpedia).openStream());
					JsonObject objetoJson = lector.readObject();
					JsonObject objetoResource = objetoJson.getJsonObject(
							"http://es.dbpedia.org/resource/" + URLDecoder.decode(entradaWiki, StandardCharsets.UTF_8));

					if (esUnSitioTuristico(objetoResource)) {

						// Es un sitio turistico
						SitioTuristico sitioTuristico = new SitioTuristico();
						sitioTuristico.setTitulo(titulo);
						sitioTuristico.setWikipedia(wikipedia);
						sitioTuristico.setLatitud(
								Double.parseDouble(elemento.getElementsByTagName("lat").item(0).getTextContent()));
						sitioTuristico.setLongitud(
								Double.parseDouble(elemento.getElementsByTagName("lng").item(0).getTextContent()));
						JsonArray arrayAbstract = objetoResource.getJsonArray("http://dbpedia.org/ontology/abstract");

						// Obtenemos el primer elemento
						if (arrayAbstract != null && !arrayAbstract.isEmpty()) {
							JsonObject objeto = arrayAbstract.getJsonObject(0);
							String resumen = objeto.getJsonString("value").toString();
							sitioTuristico.setResumen(resumen);
						}

						sitiosTuristicos.add(sitioTuristico);
					}

				}

			}
		}

		Ciudad ciudad = new Ciudad();
		ciudad.setNombre(nombreCiudad);
		ciudad.getParking().addAll(parkings);
		ciudad.getSitioTuristico().addAll(sitiosTuristicos);
		return ciudad;

	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException,
			IOException, RepositorioException, EntidadNoEncontrada {

		IServicioCiudades servicio = ServicioCiudades.getServicio();

		// Crear la ciudad de Lorca
		servicio.create(construirCiudad(LORCA));

		// Crear la ciudad de Malaga
		servicio.create(construirCiudad(MALAGA));

		/*
		 * List<CiudadResumen> ciudades = servicio.getCiudades(); for (CiudadResumen cr
		 * : ciudades) System.out.println(cr.getNombre());
		 * 
		 * List<SitioTuristico> sitiosLorca =
		 * servicio.getSitiosTuristicos(ciudades.get(0).getNombre());
		 * System.out.println("Lorca"); for (SitioTuristico st : sitiosLorca)
		 * System.out.println(st.getTitulo());
		 * 
		 * System.out.println("Malaga"); List<SitioTuristico> sitiosMalaga =
		 * servicio.getSitiosTuristicos(ciudades.get(1).getNombre()); for
		 * (SitioTuristico st : sitiosMalaga) System.out.println(st.getTitulo());
		 * 
		 * System.out.println("Aparcamientos cercanos Lorca"); List<Parking>
		 * parkingsLorca =
		 * servicio.getAparcamientosCercanos(ciudades.get(0).getNombre(),
		 * sitiosLorca.get(0).getTitulo(), 30.0); System.out.println("VA"); for (Parking
		 * p : parkingsLorca) { System.out.println(p.getDireccion()); }
		 * 
		 * System.out.println("Aparcamientos cercanos Malaga"); List<Parking>
		 * parkingsMalaga =
		 * servicio.getAparcamientosCercanos(ciudades.get(1).getNombre(),
		 * sitiosMalaga.get(0).getTitulo(), 30.0); for (Parking p : parkingsMalaga) {
		 * System.out.println(p.getDireccion()); }
		 */

		System.out.println("FIN");

	}

}
