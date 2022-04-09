package ciudades.repositorio.test;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.example.ciudades.Ciudad;
import org.example.ciudades.SitioTuristico;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ciudades.repositorio.FactoriaRepositorioCiudades;
import ciudades.repositorio.RepositorioCiudades;
import repositorio.RepositorioException;
import sax.Manejador;

public class Programa {

	
	public static void main(String[] args) throws ParserConfigurationException, SAXException {

		final String documentoSAX = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		final String documentoDOM = "http://api.geonames.org/findNearbyWikipedia?lang=es&lat=37.6713&lng=-1.69879&maxRows=500&username=francisco_david&style=full";
		RepositorioCiudades repositorio = FactoriaRepositorioCiudades.getRepositorio();
		
		//Creamos una ciudad
		Ciudad ciudad = new Ciudad();
		
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

		// manejador.printearLista();

		/* PARTE DE DOM */
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = null;

		try {
			doc = builder.parse(documentoDOM);
			// Obtenemos todos los nodos <lat> y <lng>
			NodeList titulos = doc.getElementsByTagName("title");
			NodeList resumenes = doc.getElementsByTagName("summary");
			NodeList entradasWikipedia = doc.getElementsByTagName("wikipediaUrl");
			NodeList latitudes = doc.getElementsByTagName("lat");
			NodeList longitudes = doc.getElementsByTagName("lng");
			
			for(int i = 0; i < titulos.getLength(); i++) {
				SitioTuristico sitioTur = new SitioTuristico();
				sitioTur.setCodigoPostal("30800");
				sitioTur.setPais("ES");
				sitioTur.setTitulo(titulos.item(i).getTextContent());
				sitioTur.setResumen(resumenes.item(i).getTextContent());
				sitioTur.setWikipedia(entradasWikipedia.item(i).getTextContent());
				sitioTur.setLatitud(Double.parseDouble(latitudes.item(i).getTextContent()));
				sitioTur.setLongitud(Double.parseDouble(longitudes.item(i).getTextContent()));
				
				/*
				for(int j = 0; j < 3; j++) {
					sitioTur.getAparcamiento().add(manejador.getAparcamientos().get(j));
				}
				*/
				ciudad.getSitioTuristico().add(sitioTur);
			}
			

		} catch (IOException e) {
			System.out.println("No se ha podido abrir el documentoDOM: " + documentoDOM);

		}

		
		try {
			String id = repositorio.add(ciudad);
			System.out.println("Ciudad creada con id: " + id);
		} catch (RepositorioException e) {
			e.printStackTrace();
		}
		System.out.println("FIN");

	}

}

