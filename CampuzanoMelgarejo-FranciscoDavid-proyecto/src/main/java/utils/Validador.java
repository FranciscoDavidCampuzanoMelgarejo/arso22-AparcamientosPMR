package utils;

import java.util.LinkedList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Validador extends DefaultHandler{
	
	private LinkedList<SAXParseException> errores = new LinkedList<>();
	
	public LinkedList<SAXParseException> getErrores() {
		return new List<>(errores);
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		errores.add(e);
	}

}
