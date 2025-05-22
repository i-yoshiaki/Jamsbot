package util;

import java.util.ResourceBundle;

public class PropertyManager {

	public static String getProperties(String key) {
		String value = null;

		ResourceBundle rb = ResourceBundle.getBundle("settings");
		value = rb.getString(key);

		return value;
	}
}
