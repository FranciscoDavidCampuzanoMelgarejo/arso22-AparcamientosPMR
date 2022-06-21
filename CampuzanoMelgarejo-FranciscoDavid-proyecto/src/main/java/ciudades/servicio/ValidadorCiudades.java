package ciudades.servicio;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.example.ciudades.Ciudad;
import org.xml.sax.SAXParseException;

import utils.Validador;

public class ValidadorCiudades {
	private ValidadorCiudades() {}

	// Ruta al XML Schema
	private static final String ESQUEMA = "xml/ciudades.xsd";

	public static List<SAXParseException> validar(Ciudad ciudad) {

		try {
			//Le pedimos a la factoria que construya el esquema
			Schema esquema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(ESQUEMA));
			//Le solicitamos al esquema la construccion de un objeto Validator
			Validator validador = esquema.newValidator();
			//Se registra el manejador de eventos de error
			validador.setErrorHandler(new Validador());
			
			//Validacion de los objetos JAXB
			JAXBContext contexto = JAXBContext.newInstance("org.examples.ciudades");
			validador.validate(new JAXBSource(contexto, ciudad));
			
			return ((Validador)(validador.getErrorHandler())).getErrores();
		} catch (Exception e) {
			return Collections.emptyList(); //No se ha realizado la validacion
		}

	}

}
