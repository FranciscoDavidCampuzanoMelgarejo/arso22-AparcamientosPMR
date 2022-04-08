package utils;

import java.util.UUID;

public class Utils {
	private Utils() {}

	public static String createId() {

		return UUID.randomUUID().toString();
	}
}
