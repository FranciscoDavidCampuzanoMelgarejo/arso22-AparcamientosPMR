package programa.principal;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

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

import org.example.ciudades.Aparcamiento;
import org.example.ciudades.Ciudad;
import org.example.ciudades.SitioTuristico;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ciudades.servicio.IServicioCiudades;
import ciudades.servicio.ServicioCiudades;
import repositorio.RepositorioException;
import sax.Manejador;

public class Programa {


	public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

		IServicioCiudades servicio = ServicioCiudades.getServicio();
		
		final String documentoSAX = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		final String documentoDOM = "http://api.geonames.org/findNearbyWikipedia?lang=es&lat=37.6713&lng=-1.69879&maxRows=500&username=francisco_david&style=full";
		
		Manejador manejador = new Manejador();
		final String base = "https://es.dbpedia.org/data/";
		
		LinkedList<String> rutas = new LinkedList<>();
		LinkedList<Double> lat = new LinkedList<>();
		LinkedList<Double> lon = new LinkedList<>();
		LinkedList<String> wiki = new LinkedList<>();

		/* PARTE DE SAX */
		// 1. Obtener una factoria
		SAXParserFactory factoria = SAXParserFactory.newInstance();

		// 2. Contruir el analizador (parser)
		SAXParser analizador = factoria.newSAXParser();
		XPathFactory factoria2 = XPathFactory.newInstance();
		XPath xpath = factoria2.newXPath();

		XPathExpression consulta = xpath.compile("/geonames/entry/wikipediaUrl");

		// 3. Analizar el documentoSAX

		try {

			analizador.parse(documentoSAX, manejador);

		} catch (SAXException e) {

			System.out.println("El procesamiento se ha detenido por errores sintácticos");
		} catch (IOException e) {

			System.out.println("No se ha podido abrir el documentoSAX: " + documentoSAX);
		}

		manejador.printearLista();

		
		/* PARTE DE DOM */

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = null;
		Ciudad ciudad = new Ciudad();
		for(Aparcamiento a : manejador.getAparcamientos()) {
			ciudad.getAparcamiento().add(a);
		}
		ciudad.setNombre("Lorca");
		try {
			doc = builder.parse(documentoDOM);
			// Obtenemos todos los nodos <lat> y <lng>
			NodeList latitudes = doc.getElementsByTagName("lat");
			NodeList longitudes = doc.getElementsByTagName("lng");
			NodeList wikipedia = doc.getElementsByTagName("wikipediaUrl");

			if ((latitudes.getLength() == longitudes.getLength()) && (latitudes.getLength() > 0)) {

				// Calcular la distancia entre cada Aparcamiento con cada Sitio Turistico
				for (int i = 0; i < latitudes.getLength(); i++) {
					double latEntrada = Double.parseDouble(latitudes.item(i).getTextContent());
					double longEntrada = Double.parseDouble(longitudes.item(i).getTextContent());
					
					//Guardamos los datos que queremos
					lat.add(latEntrada);
					lon.add(longEntrada);
					wiki.add(wikipedia.item(i).getTextContent());

				}
			}

		} catch (IOException e) {
			System.out.println("No se ha podido abrir el documentoDOM: " + documentoDOM);

		}
		
		NodeList resultado = (NodeList) consulta.evaluate(doc, XPathConstants.NODESET);
		String test = null;
		for (int i = 0; i < resultado.getLength(); i++) {
			int count = 0;

			Node nodo = resultado.item(i);
			for (int n = 0; n < nodo.getTextContent().length() - 1; n++) {
				if (count == 4) {
					test = nodo.getTextContent().substring(n, nodo.getTextContent().length()).trim();
					
					//Aprovechamos la obtención del titulo para general el sitio
					SitioTuristico sitio = new SitioTuristico();
					sitio.setTitulo(URLDecoder.decode(test, StandardCharsets.UTF_8));
					ciudad.getSitioTuristico().add(sitio);
					break;
					
				}
				char c = nodo.getTextContent().charAt(n);
				if (c == '/') {
					count++;
				}

			}
			rutas.add(base + test + ".json");
			//System.out.println(rutas.get(i));
		}

		// System.out.println(rutas);
		int index = 0;
		for (SitioTuristico s : ciudad.getSitioTuristico()) {
			//Asignamos los elementos guardados en listas
			s.setLatitud(lat.get(index));
			s.setLongitud(lon.get(index));
			s.setWikipedia(wiki.get(index));
			URL url = new URL(rutas.get(index));
			
	
			JsonReader jsonReader = Json.createReader(url.openStream());
			JsonObject sitioT = jsonReader.readObject();
			JsonObject resources = sitioT
					.getJsonObject("http://es.dbpedia.org/resource/" + URLDecoder.decode(s.getTitulo(), StandardCharsets.UTF_8));
			JsonArray abst = resources.getJsonArray("http://dbpedia.org/ontology/abstract");
			for (JsonObject abs : abst.getValuesAs(JsonObject.class)) {
				s.setResumen(abs.getJsonString("value").toString());

			}
			index++;
		}

		
		System.out.println("FIN");
		
		try {
			servicio.create(ciudad);
		} catch (RepositorioException e) {
			e.printStackTrace();
		}

	}

}
