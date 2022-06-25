package ciudades.repositorio;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.um.ciudades.Ciudad;

import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public class RepositorioCiudadesXML implements RepositorioCiudades {

	public static final String DIRECTORIO_CIUDADES = "ciudades/";
	//private static final String c="La ciudad no existe, id: ";
	static {

		File directorio = new File(DIRECTORIO_CIUDADES);

		if (!directorio.exists())
			directorio.mkdir();
	}
	
	

	/*** Métodos de apoyo ***/

	protected String getDocumento(String nombre) {

		return DIRECTORIO_CIUDADES + nombre + ".xml";
	}

	protected boolean checkDocumento(String nombre) {

		final String documento = getDocumento(nombre);

		File fichero = new File(documento);

		return fichero.exists();
	}

	protected void save(Ciudad ciudad) throws RepositorioException {

		final String documento = getDocumento(ciudad.getNombre());

		final File fichero = new File(documento);

		try {

			JAXBContext contexto = JAXBContext.newInstance("es.um.ciudades");
			Marshaller marshaller = contexto.createMarshaller();

			marshaller.setProperty("jaxb.formatted.output", true);

			marshaller.marshal(ciudad, fichero);

		} catch (Exception e) {

			throw new RepositorioException("Error al guardar la ciudad: " + ciudad.getNombre(), e);
		}
	}

	protected Ciudad load(String nombre) throws RepositorioException, EntidadNoEncontrada {

		if (!checkDocumento(nombre))
			throw new EntidadNoEncontrada("La ciudad: " + nombre + "no existe en el repositorio");

		final String documento = getDocumento(nombre);

		try {

			JAXBContext contexto = JAXBContext.newInstance("es.um.ciudades");
			Unmarshaller unmarshaller = contexto.createUnmarshaller();

			return (Ciudad) unmarshaller.unmarshal(new File(documento));

		} catch (Exception e) {
			throw new RepositorioException("Error al cargar la ciudad: " + nombre, e);
		}
	}

	/*** Fin métodos de apoyo ***/

	@Override
	public String add(Ciudad ciudad) throws RepositorioException {

		save(ciudad);

		return ciudad.getNombre();
	}

	@Override
	public void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada {

		if (!checkDocumento(ciudad.getNombre()))
			throw new EntidadNoEncontrada("La ciudad " + ciudad.getNombre() + "no existe en el repositorio");

		save(ciudad);

	}

	@Override
	public void delete(Ciudad ciudad) throws EntidadNoEncontrada {

		if (!checkDocumento(ciudad.getNombre()))
			throw new EntidadNoEncontrada("La ciudad: " + ciudad.getNombre() + "no existe en el repositorio");

		final String documento = getDocumento(ciudad.getNombre());

		File fichero = new File(documento);

		fichero.delete();
	}

	@Override
	public Ciudad getByNombre(String nombre) throws RepositorioException, EntidadNoEncontrada {

		return load(nombre);
	}

	@Override
	public List<String> getNombres() {

		LinkedList<String> resultado = new LinkedList<>();

		File directorio = new File(DIRECTORIO_CIUDADES);

		File[] ciudades = directorio.listFiles(f -> f.isFile() && f.getName().endsWith(".xml"));

		for (File file : ciudades) {

			String nombre = file.getName().substring(0, file.getName().length() - 4);

			resultado.add(nombre);
		}

		return resultado;
	}

	@Override
	public List<Ciudad> getAll() throws RepositorioException {

		LinkedList<Ciudad> resultado = new LinkedList<>();

		for (String nombre : getNombres()) {

			try {
				resultado.add(load(nombre));
			} catch (EntidadNoEncontrada e) {

				throw new RepositorioException("Error al cargar la ciudad: " + nombre, e);
			}
		}

		return resultado;
	}

}
