package ciudades.repositorio;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.example.ciudades.Ciudad;

import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;
import utils.Utils;

public class RepositorioCiudadesXML implements RepositorioCiudades {

	public final static String DIRECTORIO_CIUDADES = "ciudades/";

	static {

		File directorio = new File(DIRECTORIO_CIUDADES);

		if (!directorio.exists())
			directorio.mkdir();
	}

	/*** Métodos de apoyo ***/

	protected String getDocumento(String id) {

		return DIRECTORIO_CIUDADES + id + ".xml";
	}

	protected boolean checkDocumento(String id) {

		final String documento = getDocumento(id);

		File fichero = new File(documento);

		return fichero.exists();
	}

	protected void save(Ciudad ciudad) throws RepositorioException {

		final String documento = getDocumento(ciudad.getId());

		final File fichero = new File(documento);

		try {

			JAXBContext contexto = JAXBContext.newInstance("org.example.ciudades");
			Marshaller marshaller = contexto.createMarshaller();

			marshaller.setProperty("jaxb.formatted.output", true);

			marshaller.marshal(ciudad, fichero);

		} catch (Exception e) {

			throw new RepositorioException("Error al guardar la ciudad con id: " + ciudad.getId(), e);
		}
	}

	protected Ciudad load(String id) throws RepositorioException, EntidadNoEncontrada {

		if (!checkDocumento(id))
			throw new EntidadNoEncontrada("La ciudad no existe, id: " + id);

		final String documento = getDocumento(id);

		try {

			JAXBContext contexto = JAXBContext.newInstance("org.example.ciudades");
			Unmarshaller unmarshaller = contexto.createUnmarshaller();

			return (Ciudad) unmarshaller.unmarshal(new File(documento));

		} catch (Exception e) {
			throw new RepositorioException("Error al cargar la ciudad con id: " + id, e);
		}
	}

	/*** Fin métodos de apoyo ***/

	@Override
	public String add(Ciudad ciudad) throws RepositorioException {

		String id = Utils.createId();

		ciudad.setId(id);
		save(ciudad);

		return id;
	}

	@Override
	public void update(Ciudad ciudad) throws RepositorioException, EntidadNoEncontrada {

		if (!checkDocumento(ciudad.getId()))
			throw new EntidadNoEncontrada("La ciudad no existe, id: " + ciudad.getId());

		save(ciudad);

	}

	@Override
	public void delete(Ciudad ciudad) throws EntidadNoEncontrada {

		if (!checkDocumento(ciudad.getId()))
			throw new EntidadNoEncontrada("La ciudad no existe, id: " + ciudad.getId());

		final String documento = getDocumento(ciudad.getId());

		File fichero = new File(documento);

		fichero.delete();
	}

	@Override
	public Ciudad getById(String id) throws RepositorioException, EntidadNoEncontrada {

		return load(id);
	}

	@Override
	public List<String> getIds() {

		LinkedList<String> resultado = new LinkedList<>();

		File directorio = new File(DIRECTORIO_CIUDADES);

		File[] ciudades = directorio.listFiles(f -> f.isFile() && f.getName().endsWith(".xml"));

		for (File file : ciudades) {

			String id = file.getName().substring(0, file.getName().length() - 4);

			resultado.add(id);
		}

		return resultado;
	}

	@Override
	public List<Ciudad> getAll() throws RepositorioException {

		LinkedList<Ciudad> resultado = new LinkedList<Ciudad>();

		for (String id : getIds()) {

			try {
				resultado.add(load(id));
			} catch (EntidadNoEncontrada e) {

				throw new RepositorioException("Error al cargar la ciudad: " + id, e);
			}
		}

		return resultado;
	}


}
