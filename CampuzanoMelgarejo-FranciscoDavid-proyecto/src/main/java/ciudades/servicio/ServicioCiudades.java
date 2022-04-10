package ciudades.servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.ciudades.Aparcamiento;
import org.example.ciudades.Ciudad;
import org.example.ciudades.SitioTuristico;
import org.xml.sax.SAXParseException;

import ciudades.repositorio.FactoriaRepositorioCiudades;
import ciudades.repositorio.RepositorioCiudades;
import ciudades.servicio.ListadoAparcamiento.AparcamientoResumen;
import ciudades.servicio.ListadoCiudades.CiudadResumen;
import ciudades.servicio.ListadoSitioTuristico.SitioTuristicoResumen;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public class ServicioCiudades implements IServicioCiudades {

	// Obtenemos el repositorio (singleton)
	RepositorioCiudades repositorio = FactoriaRepositorioCiudades.getRepositorio();

	// Diccionario para asociar a cada sitio turistico (nombre) una lista de
	// aparcamientos
	// mas cercanos
	private HashMap<String, List<Aparcamiento>> aparcamientosCercanos = new HashMap<>();

	// Patron Singleton para crear el servicio
	private static ServicioCiudades servicio;

	public static ServicioCiudades getServicio() {
		if (servicio == null) {
			servicio = new ServicioCiudades();
		}
		return servicio;
	}

	// Metodos privados

	// Metodo para calcular la distancia en km entre dos puntos geograficos (lat,
	// lng)
	private double calcularDistancia(SitioTuristico s, Aparcamiento a) {
		final int R = 6371; // Radio de la tierra en kilometros

		double latDistance = Math.toRadians(a.getLatitud() - s.getLatitud());
		double lonDistance = Math.toRadians(a.getLongitud() - s.getLongitud());
		double x = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(s.getLatitud()))
				* Math.cos(Math.toRadians(a.getLatitud())) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
		return R * c; // Para pasarlo a kilometros
	}

	// Algoritmo QuickSort
	private void ordenarAparcamientos(double[] distancias, int[] indicesAparcamientos, int izq, int der) {

		if (izq < der) {

			double pivote = distancias[der];
			double auxDistancias;
			int auxIndices;
			int i = izq - 1;
			for (int j = izq; j <= der; j++) {
				if (distancias[j] < pivote) {
					i++;
					// Intercambio
					auxDistancias = distancias[j];
					auxIndices = indicesAparcamientos[j];

					distancias[j] = distancias[i];
					indicesAparcamientos[j] = indicesAparcamientos[i];

					distancias[i] = auxDistancias;
					indicesAparcamientos[i] = auxIndices;
				}
			}
			// Intercambio
			i++;
			auxDistancias = distancias[i];
			auxIndices = indicesAparcamientos[i];

			distancias[i] = distancias[izq];
			indicesAparcamientos[i] = indicesAparcamientos[izq];

			distancias[izq] = auxDistancias;
			indicesAparcamientos[izq] = auxIndices;

			// Llamada recursiva
			ordenarAparcamientos(distancias, indicesAparcamientos, izq, i - 1);
			ordenarAparcamientos(distancias, indicesAparcamientos, i + 1, der);
		}

	}

	private void calcularAparcamientosCercanos(Ciudad ciudad) {
		List<SitioTuristico> sitiosTuristicos = ciudad.getSitioTuristico();
		List<Aparcamiento> aparcamientos = ciudad.getAparcamiento();

		// Matriz que guarda la distnacia en km entre un sitio y un aparcamiento
		double[][] matrizDistancias = new double[sitiosTuristicos.size()][aparcamientos.size()];
		double distancia;

		// Almacenar las distancias en la matriz
		for (int i = 0; i < sitiosTuristicos.size(); i++) {
			for (int j = 0; j < aparcamientos.size(); j++) {
				distancia = calcularDistancia(sitiosTuristicos.get(i), aparcamientos.get(j));
				// Si la distnacia entre el sitio y el aparcamiento es mayor que 5km, entonces
				// marcarla la distancia como INFINITO (demasiado lejos)
				if (distancia > 5)
					matrizDistancias[i][j] = Double.POSITIVE_INFINITY;
				else
					matrizDistancias[i][j] = distancia;
			}
		}

		// Por cada sitio, obtengo las distancias con cada aparcamientos y las ordeno de
		// menor a mayor
		// A la vez, ordeno los indices de los aparcamientos del mas cercano al mas
		// lejano
		// Por ultimo, asocio el sitio con los aparcamientos ordenados
		int[] indicesAparcamientos = new int[aparcamientos.size()];
		for (int i = 0; i < sitiosTuristicos.size(); i++) {

			// Inicializar el array
			for (int k = 0; k < aparcamientos.size(); k++)
				indicesAparcamientos[k] = k;

			ordenarAparcamientos(matrizDistancias[i], indicesAparcamientos, 0, aparcamientos.size() - 1);
			ArrayList<Aparcamiento> aparcamientosOrdenados = new ArrayList<>(20);
			int j = 0;
			while ((j < aparcamientos.size()) && (j < 20)) {
				aparcamientosOrdenados.add(aparcamientos.get(indicesAparcamientos[j]));
				j++;
			}

			aparcamientosCercanos.put(sitiosTuristicos.get(i).getTitulo(), aparcamientosOrdenados);
		}

	}

	// Metodo para validar una Ciudad
	protected void validar(Ciudad ciudad) throws IllegalArgumentException {
		List<SAXParseException> validaciones = ValidadorCiudades.validar(ciudad);

		// La ciudad no cumple con el formato del XML Schema
		if (!validaciones.isEmpty()) {
			throw new IllegalArgumentException("La ciudad no cumple el esquema: " + validaciones.get(0).getMessage());
		}
	}

	@Override
	public String create(Ciudad ciudad) throws RepositorioException {

		// Según el esquema, el id es obligatorio
		// Se establece uno provisional, el repositorio aportará el definitivo
		ciudad.setId(" ");

		// TO DO -> la funcion validar lanza la excepcion IllegalArgumentException
		validar(ciudad);

		// Creamos una entrada en el repositorio con la ciudad
		String id = repositorio.add(ciudad);
		calcularAparcamientosCercanos(ciudad);
		return id;
	}

	@Override
	public void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada {

		// TO DO -> la funcion validar lanza la excepcion IllegalArgumentException
		validar(ciudad);
		repositorio.update(ciudad);
	}

	@Override
	public Ciudad getCiudad(String id) throws RepositorioException, EntidadNoEncontrada {

		Ciudad ciudad = repositorio.getById(id);
		calcularAparcamientosCercanos(ciudad);
		return ciudad;
	}

	@Override
	public void removeCiudad(String id) throws RepositorioException, EntidadNoEncontrada {

		Ciudad ciudad = repositorio.getById(id);
		// TO DO -> Deberia de eleminar las entradas en el mapa
		repositorio.delete(ciudad);
	}

	@Override
	public ListadoCiudades getResumenCiudades() throws RepositorioException {

		ListadoCiudades listado = new ListadoCiudades();

		for (Ciudad c : repositorio.getAll()) {
			CiudadResumen resumen = new CiudadResumen();
			resumen.setId(c.getId());
			resumen.setNombre(c.getNombre());
			listado.getResumenCiudades().add(resumen);
		}

		return listado;
	}

	@Override
	public ListadoSitioTuristico getResumenSitiosTuristicos(String id)
			throws RepositorioException, EntidadNoEncontrada {

		ListadoSitioTuristico listado = new ListadoSitioTuristico();

		for (SitioTuristico st : repositorio.getById(id).getSitioTuristico()) {
			SitioTuristicoResumen resumen = new SitioTuristicoResumen();
			resumen.setNombre(st.getTitulo());
			resumen.setResumen(st.getResumen());
			listado.getResumenSitiosTuristicos().add(resumen);
		}

		return listado;
	}

	@Override
	public ListadoAparcamiento getAparcamientosCercanos(String nombreSitio) {
		if (aparcamientosCercanos.containsKey(nombreSitio)) {
			ListadoAparcamiento listado = new ListadoAparcamiento();

			for (Aparcamiento a : aparcamientosCercanos.get(nombreSitio)) {
				AparcamientoResumen resumen = new AparcamientoResumen();
				resumen.setId(a.getId());
				listado.getResumenAparcamientos().add(resumen);
			}
			return listado;
		}
		return null;
	}

	@Override
	public Aparcamiento getInformacion(String idCiudad, String idAparcamiento)
			throws EntidadNoEncontrada, RepositorioException {
		for (Aparcamiento a : repositorio.getById(idCiudad).getAparcamiento()) {
			System.out.println(a);
			if (a.getId().equals(idAparcamiento))
				return a;
		}
		return null;
	}

}
