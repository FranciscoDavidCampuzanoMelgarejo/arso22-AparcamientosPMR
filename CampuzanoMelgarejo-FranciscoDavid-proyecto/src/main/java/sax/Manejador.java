package sax;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import utils.Utils;

import org.example.ciudades.Aparcamiento;

public class Manejador extends DefaultHandler {

	// Lista con los aparcamientos encontrados
	private List<Aparcamiento> aparcamientos;

	private boolean dentroDireccion;
	private boolean dentroLongitud;
	private boolean dentroLatitud;
	
	private String direccion;
	private double longitud;
	private double latitud;

	@Override
	public void startDocument() throws SAXException {
		if (aparcamientos == null) {
			aparcamientos = new LinkedList<>();
		}

		this.dentroDireccion = false;
		this.dentroLongitud = false;
		this.dentroLatitud = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equals("direccion")) {
			dentroDireccion = true;
		}
		else if(qName.equals("longitud")) {
			dentroLongitud = true;
		}
		else if(qName.equals("latitud")) {
			dentroLatitud = true;
		}

	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(dentroDireccion) {
			this.direccion = new String(ch, start, length);
			this.dentroDireccion = false;
		}
		else if(dentroLongitud) {
			this.longitud = Double.parseDouble(new String(ch, start, length));
			this.dentroLongitud = false;
		}
		else if(dentroLatitud) {
			this.latitud = Double.parseDouble(new String(ch, start, length));
			this.dentroLatitud = false;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("parking")) {
			Aparcamiento parking = new Aparcamiento();
			parking.setId(Utils.createId());
			parking.setDireccion(direccion);
			parking.setLatitud(latitud);
			parking.setLongitud(longitud);
			this.aparcamientos.add(parking);
		}
	}
	
	public void printearLista() {
		for(Aparcamiento a : aparcamientos) {
			System.out.println(a);
		}
	}

	public List<Aparcamiento> getAparcamientos() {
		return aparcamientos;
	}

}
