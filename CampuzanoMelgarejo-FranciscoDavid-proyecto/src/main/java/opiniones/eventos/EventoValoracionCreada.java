package opiniones.eventos;

import java.time.LocalDateTime;

import javax.json.bind.annotation.JsonbDateFormat;

public class EventoValoracionCreada {

	private String url;
	private int valoraciones;
	private double calificacionMedia;

	// Valoracion
	private String correo;
	
	private int calificacion;
	private String comentario;

	public EventoValoracionCreada() {

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getValoraciones() {
		return valoraciones;
	}

	public void setValoraciones(int valoraciones) {
		this.valoraciones = valoraciones;
	}

	public double getCalificacionMedia() {
		return calificacionMedia;
	}

	public void setCalificacionMedia(double calificacionMedia) {
		this.calificacionMedia = calificacionMedia;
	}

	// Valoracion
	public String getCorreo() {
		return correo;
	}

	public void setCorre(String correo) {
		this.correo = correo;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

}
