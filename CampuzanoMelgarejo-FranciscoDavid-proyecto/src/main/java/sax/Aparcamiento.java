package sax;

public class Aparcamiento {

	private String direccion;
	private double latitud;
	private double longitud;
	private String ciudad = "Lorca";

	public Aparcamiento() {

	}

	public Aparcamiento(String direccion, double latitud, double longitud) {
		this.direccion = direccion;
		this.latitud = latitud;
		this.longitud = longitud;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	@Override
	public String toString() {
		return "Aparcamiento [direccion=" + direccion + ", latitud=" + latitud + ", longitud=" + longitud + ", ciudad="
				+ ciudad + "]";
	}

}
