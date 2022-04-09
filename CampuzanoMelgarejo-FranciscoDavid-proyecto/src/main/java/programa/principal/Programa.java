package programa.principal;

import java.io.IOException;
import java.net.MalformedURLException;
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

import org.example.ciudades.Ciudad;
import org.example.ciudades.SitioTuristico;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sax.Manejador;

public class Programa {

	private static double calcularDistancia(double lat1, double long1, double lat2, double long2) {
		final int R = 6371; // Radio de la tierra en kilometros

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(long2 - long1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c; // Para pasarlo a kilometros
	}

	private static void printearMatriz(double[][] matriz, int n, int m) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				System.out.print(matriz[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

		final String documentoSAX = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		final String documentoDOM = "http://api.geonames.org/findNearbyWikipedia?lang=es&lat=37.6713&lng=-1.69879&maxRows=500&username=francisco_david&style=full";
		double[][] matrizDistancias = null; // Matriz que guarda la distancia en km ente cada aparcamiento y cada sitio
											// turistico
		Manejador manejador = new Manejador();
		final String base = "https://es.dbpedia.org/data/";
		LinkedList<String> rutas = new LinkedList<>();
		LinkedList<Double> lat = new LinkedList<>();
		LinkedList<Double> lon = new LinkedList<>();
		LinkedList<String> paises = new LinkedList<>();
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

		// manejador.printearLista();

		/* PARTE DE DOM */
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = null;
		Ciudad ciudad = new Ciudad();
		ciudad.setNombre("Lorca");
		try {
			doc = builder.parse(documentoDOM);
			// Obtenemos todos los nodos <lat> y <lng>
			NodeList latitudes = doc.getElementsByTagName("lat");
			NodeList longitudes = doc.getElementsByTagName("lng");
			NodeList countryCode = doc.getElementsByTagName("countryCode");
			NodeList wikipedia = doc.getElementsByTagName("wikipediaUrl");

			if ((latitudes.getLength() == longitudes.getLength()) && (latitudes.getLength() > 0)) {
				matrizDistancias = new double[manejador.getAparcamientos().size()][latitudes.getLength()];

				// Calcular la distancia entre cada Aparcamiento con cada Sitio Turistico
				for (int i = 0; i < latitudes.getLength(); i++) {
					double latEntrada = Double.parseDouble(latitudes.item(i).getTextContent());
					double longEntrada = Double.parseDouble(longitudes.item(i).getTextContent());
					
					//Guardamos los datos que queremos
					lat.add(latEntrada);
					lon.add(longEntrada);
					paises.add(countryCode.item(i).getTextContent());
					wiki.add(wikipedia.item(i).getTextContent());

					for (int j = 0; j < manejador.getAparcamientos().size(); j++) {
						double latAparcamiento = manejador.getAparcamientos().get(j).getLatitud();
						double longAparcamiento = manejador.getAparcamientos().get(j).getLongitud();
						matrizDistancias[j][i] = Programa.calcularDistancia(latEntrada, longEntrada, latAparcamiento,
								longAparcamiento);
					}
				}
			}

			// Array que guarda para cada sitio, cual es su aparcamiento mas cercano
			int[] aparcamientoMasCercano = new int[latitudes.getLength()];

			// Calcular el aparcamiento mas cercano para cada Sitio
			for (int i = 0; i < latitudes.getLength(); i++) {
				double minimo = Double.POSITIVE_INFINITY;
				int pos = 0;
				for (int j = 0; j < manejador.getAparcamientos().size(); j++) {
					if (matrizDistancias[j][i] < minimo) {
						minimo = matrizDistancias[j][i];
						pos = j; // Aparcamiento
					}
				}
				aparcamientoMasCercano[i] = pos;
			}

			Programa.printearMatriz(matrizDistancias, manejador.getAparcamientos().size(), latitudes.getLength());

			System.out.println("Aparcamientos Mas Cercanos a cada Sitio");
			for (int i = 0; i < latitudes.getLength(); i++) {
				System.out.println("Sitio: " + i + " ---> Aparcamiento: " + aparcamientoMasCercano[i]);
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
					sitio.setTitulo(test);
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
			s.setPais(paises.get(index));
			s.setWikipedia(wiki.get(index));
			URL url = new URL(rutas.get(index));
			/*
			 * InputStream fuente = new URL(rutas.get(index)).openStream(); BufferedReader
			 * rd = new BufferedReader(new InputStreamReader(fuente,
			 * StandardCharsets.UTF_8));
			 */
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

	}

}
