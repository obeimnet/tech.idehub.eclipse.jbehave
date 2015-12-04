package tech.idehub.eclipse.jbehave.junit.preferences;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_RUNNER_CLASS;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_EXTENTION;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS;

import java.util.HashMap;

import tech.idehub.eclipse.jbehave.junit.Activator;


public class JBehaveRunnerPreferenceCache {

	private static final HashMap<String, String> cache = new HashMap<String, String>();

	public synchronized  static String get(String key) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		} else {
			String value = Activator.getDefault().getPreferenceStore().getString(key);
			cache.put(key, value);
			return value;
		}
	}

	public synchronized  static void purge() {
		cache.clear();
	}

	public synchronized static String getStoryFileResolutionStrategy() {
		 return get(P_STORY_FILE_RESOLUTION_STRATEGY);
	}


	public synchronized static String getRrunnerClass() {
		 return get(P_RUNNER_CLASS);
	}

	public synchronized static String getStoryFileExtention() {
		 return get(P_STORY_FILE_EXTENTION);
	}


	public synchronized static String getStoryPathSystemProperty() {
		 return get(P_STORY_PATH_SYSTEM_PROPERTY);
	}

	public synchronized static String getAdditionalJvmOptions() {
		 return get(P_ADDITIONAL_JVM_OPTIONS);
	}

}
