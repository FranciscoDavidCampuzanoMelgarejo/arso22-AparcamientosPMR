package utils;

import java.util.UUID;

public class Utils {
	private Utils() {
	}

	public static String createId() {

		return UUID.randomUUID().toString();
	}

	// Metodo para calcular la distancia en km entre dos puntos geograficos (lat,
	// lng)
	public static double calcularDistancia(double latSitio, double lngSitio, double latParking, double lngParking) {
		final int R = 6371; // Radio de la tierra en kilometros

		double latDistancia = Math.toRadians(latParking - latSitio);
		double lonDistancia = Math.toRadians(lngParking - lngSitio);
		double x = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2) + Math.cos(Math.toRadians(latSitio))
				* Math.cos(Math.toRadians(latParking)) * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);
		double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));
		double distanciaFinal = R * c; // Para pasarlo a kilometros

		// Redondear a 3 decimales
		double parteEntera = Math.floor(distanciaFinal);
		double decimales = (distanciaFinal - parteEntera) * 1000;
		decimales = Math.round(decimales);
		return (parteEntera + (decimales / 1000));
	}

	public static void ordenarParkings(double[] distancias, int[] indicesParkings, int izq, int der) {
		if (izq < der) {

			double pivote = distancias[der];
			double auxDistancias;
			int auxIndices;
			int i = izq - 1;
			for (int j = izq; j <= der - 1; j++) {
				if (distancias[j] < pivote) {
					i++;
					// Intercambio
					auxDistancias = distancias[j];
					auxIndices = indicesParkings[j];

					distancias[j] = distancias[i];
					indicesParkings[j] = indicesParkings[i];

					distancias[i] = auxDistancias;
					indicesParkings[i] = auxIndices;
				}
			}
			// Intercambio
			i++;
			auxDistancias = distancias[i];
			auxIndices = indicesParkings[i];

			distancias[i] = distancias[der];
			indicesParkings[i] = indicesParkings[der];

			distancias[der] = auxDistancias;
			indicesParkings[der] = auxIndices;

			// Llamada recursiva
			ordenarParkings(distancias, indicesParkings, izq, i - 1);
			ordenarParkings(distancias, indicesParkings, i + 1, der);
		}
	}
}
