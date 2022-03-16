package sax;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class ProgramaSAX {

	public static void main(String[] args) throws ParserConfigurationException, SAXException{
		
		final String documento = "https://datos.lorca.es/catalogo/parking-movilidad-reducida/XML";
		
		Manejador manejador = new Manejador();
		
		//1. Obtener una factoria
		SAXParserFactory factoria = SAXParserFactory.newInstance();
		
		//2. Contruir el analizador (parser)
		SAXParser analizador = factoria.newSAXParser();
		
		//3. Analizar el documento
		
		try {
			
			analizador.parse(documento, manejador);
			
		} catch (SAXException e) {
			
			System.out.println("El procesamiento se ha detenido por errores sintácticos");
		}
		catch(IOException e) {
			
			System.out.println("No se ha podido abrir el documento: " + documento);	
		}
		
		manejador.printearLista();
		System.out.println("FIN");
		
	}
}
