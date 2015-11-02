package tech.idehub.eclipse.jbehave.junit.preferences;

import java.util.HashMap;

import org.eclipse.core.runtime.Platform;

import tech.idehub.eclipse.jbehave.junit.Activator;


public class JBehaveRunnerPreferenceCache {

	private static final HashMap<String, String> cache = new HashMap<String, String>();
	
	public synchronized  static String get(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		} else {
			String value = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, key, null, null);
			cache.put(key, value);
			return value;
		}
	}
	
	public synchronized  static void purge() {
		cache.clear();
	}
}
