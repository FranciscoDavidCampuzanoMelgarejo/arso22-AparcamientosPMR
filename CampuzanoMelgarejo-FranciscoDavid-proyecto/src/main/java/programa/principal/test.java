package programa.principal;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

public class test {
	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, JAXBException {
		final String documentoSAX = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		final String documentoDOM = "http://api.geonames.org/findNearbyWikipedia?lang=es&lat=37.6713&lng=-1.69879&maxRows=500&username=francisco_david&style=full";
		final String base = "https://es.dbpedia.org/data";
		LinkedList<String> rutas = new LinkedList<>();
		// Construye un analizador DOM

		DocumentBuilderFactory factoriaDOM = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factoriaDOM.newDocumentBuilder();

		Document doc = builder.parse(documentoDOM);

		// 1. Obtener la factoria

		XPathFactory factoria = XPathFactory.newInstance();

		// 2. Construir el evaluador XPath

		XPath xpath = factoria.newXPath();

		// 3. Realizar una consulta

		XPathExpression consulta = xpath.compile("/geonames/entry/wikipediaUrl");

		// Importante: hay que ajustar la evaluacion y el tipo de retorno segun
		// el dato que se espere

		NodeList resultado = (NodeList) consulta.evaluate(doc, XPathConstants.NODESET);
		String test = null;
		for (int i = 0; i < resultado.getLength(); i++) {
			int count = 0;

			Node nodo = resultado.item(i);
			for (int n = 0; n < nodo.getTextContent().length() - 1; n++) {
				// if(c == '\n') break;
				if (count == 3) {
					test = nodo.getTextContent().substring(n, nodo.getTextContent().length()).trim();
				}
				char c = nodo.getTextContent().charAt(n);
				if (c == '/') {
					count++;
				}

			}
			rutas.add(base + test + ".json");
		}
		// es.dbpedia.org/property/inicio
		// es.dbpedia.org/property/final
		Ciudad ciudad = new Ciudad();
		ciudad.setNombre("Lorca");

		// System.out.println(rutas);
		int index = 0;
		for (String s : rutas) {
			SitioTuristico sitio = new SitioTuristico();
			InputStream fuente = new URL(rutas.get(index)).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(fuente, StandardCharsets.UTF_8));
			JsonReader jsonReader = Json.createReader(rd);
			JsonObject sitioT = jsonReader.readObject();
			System.out.println(sitioT.toString());
			JsonArray inicio = sitioT.getJsonArray("property/inicio");
			JsonArray fin = sitioT.getJsonArray("property/final");
			if (inicio != null) {
				System.out.println("test");
				/*
				 * String inicio = propiedades.getString("inicio"); String fin =
				 * propiedades.getString("final"); System.out.println(inicio);
				 * System.out.println(fin); // String inicio =
				 * arrayInicio.getJsonString(0).toString(); sitio.setFechaInicio(inicio); //
				 * String fin = arrayFinal.getJsonString(0).toString();
				 * sitio.setFechaFinal(fin); ciudad.getSitioTuristico().add(sitio);
				 */ }
			if (fin != null) {
				System.out.println("test2");
				/*
				 * String inicio = propiedades.getString("inicio"); String fin =
				 * propiedades.getString("final"); System.out.println(inicio);
				 * System.out.println(fin); // String inicio =
				 * arrayInicio.getJsonString(0).toString(); sitio.setFechaInicio(inicio); //
				 * String fin = arrayFinal.getJsonString(0).toString();
				 * sitio.setFechaFinal(fin); ciudad.getSitioTuristico().add(sitio);
				 */ }

			index++;
		}

		// Construir el contexto JAXB para las clases anotadas

		JAXBContext contexto = JAXBContext.newInstance(Ciudad.class);

		// Empaquetado en un documento XML (marshalling)

		Marshaller marshaller = contexto.createMarshaller();

		marshaller.setProperty("jaxb.formatted.output", true);

		marshaller.marshal(ciudad, new File("xml/ciudad-test.xml"));

		System.out.println("fin.");
	}
}
