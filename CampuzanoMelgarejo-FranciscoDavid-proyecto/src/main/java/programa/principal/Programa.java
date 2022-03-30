package programa.principal;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
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
		double distance = R * c; //Para pasarlo a kilometros
		return distance;
	}
	
	private static void printearMatriz(double matriz[][], int n, int m) {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				System.out.print(matriz[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException {

		final String documentoSAX = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		final String documentoDOM = "http://api.geonames.org/findNearbyWikipedia?lang=es&lat=37.6713&lng=-1.69879&maxRows=500&username=francisco_david&style=full";
		double matrizDistancias[][] = null; //Matriz que guarda la distancia en km ente cada aparcamiento y cada sitio turistico
		Manejador manejador = new Manejador();

		/* PARTE DE SAX */
		// 1. Obtener una factoria
		SAXParserFactory factoria = SAXParserFactory.newInstance();

		// 2. Contruir el analizador (parser)
		SAXParser analizador = factoria.newSAXParser();

		// 3. Analizar el documentoSAX

		try {

			analizador.parse(documentoSAX, manejador);

		} catch (SAXException e) {

			System.out.println("El procesamiento se ha detenido por errores sintácticos");
		} catch (IOException e) {

			System.out.println("No se ha podido abrir el documentoSAX: " + documentoSAX);
		}

		//manejador.printearLista();

		/* PARTE DE DOM */
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = null;

		try {
			doc = builder.parse(documentoDOM);
			//Obtenemos todos los nodos <lat> y <lng>
			NodeList latitudes = doc.getElementsByTagName("lat");
			NodeList longitudes = doc.getElementsByTagName("lng");

			
			if ((latitudes.getLength() == longitudes.getLength()) && (latitudes.getLength() > 0)) {
				matrizDistancias = new double[manejador.getAparcamientos().size()][latitudes.getLength()];
				
				//Calcular la distancia entre cada Aparcamiento con cada Sitio Turistico
				for (int i = 0; i < latitudes.getLength(); i++) {
					double lat_entrada = Double.parseDouble(latitudes.item(i).getTextContent());
					double long_entrada = Double.parseDouble(longitudes.item(i).getTextContent());
					
					for(int j = 0; j < manejador.getAparcamientos().size(); j++) {
						double lat_aparcamiento = manejador.getAparcamientos().get(j).getLatitud();
						double long_aparcamiento = manejador.getAparcamientos().get(j).getLongitud();
						matrizDistancias[j][i] = Programa.calcularDistancia(lat_entrada, long_entrada, lat_aparcamiento, long_aparcamiento);
					}
				}
			}
			
			//Array que guarda para cada sitio, cual es su aparcamiento mas cercano
			int aparcamientoMasCercano[] = new int[latitudes.getLength()];
			
			//Calcular el aparcamiento mas cercano para cada Sitio
			for(int i = 0; i < latitudes.getLength(); i++) {
				double minimo = Double.POSITIVE_INFINITY;
				int pos = 0;
				for(int j = 0; j < manejador.getAparcamientos().size(); j++) {
					if(matrizDistancias[j][i] < minimo) {
						minimo = matrizDistancias[j][i];
						pos = j; //Aparcamiento
					}
				}
				aparcamientoMasCercano[i] = pos;	
			}
			
			Programa.printearMatriz(matrizDistancias, manejador.getAparcamientos().size(), latitudes.getLength());
			
			System.out.println("Aparcamientos Mas Cercanos a cada Sitio");
			for(int i = 0; i < latitudes.getLength(); i++) {
				System.out.println("Sitio: " + i + " ---> Aparcamiento: " + aparcamientoMasCercano[i]);
			}

		} catch (IOException e) {
			System.out.println("No se ha podido abrir el documentoDOM: " + documentoDOM);

		}

		System.out.println("FIN");

	}

}
