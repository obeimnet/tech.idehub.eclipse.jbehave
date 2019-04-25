package tech.idehub.eclipse.jbehave.junit.preferences;

import java.util.HashMap;

import tech.idehub.eclipse.jbehave.junit.Activator;
import tech.idehub.eclipse.jbehave.junit.project.ProjectPreferencePage;


public class JBehaveRunnerPreferenceCache {

	private static final HashMap<String, String> CACHE = new HashMap<String, String>();

	private  static String get(String key) {
		if (CACHE.containsKey(key)) {
			return CACHE.get(key);
		} else {
			String value = Activator.getDefault().getPreferenceStore().getString(key);
			if (value != null && value.trim().length() > 0) {
				CACHE.put(key, value);
			}			
			return value;
		}
	}

	public synchronized  static void purge() {
		CACHE.clear();
	}

	public synchronized static String getRunnerClass(String projectName) {
		String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_RUNNER_CLASS.concat(projectName));
		return  get(key);
	}
	
	public synchronized static String getStoryFileResolutionStrategy(String projectName) {
		String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY.concat(projectName));
		return  get(key);
	}

	public synchronized static String getStoryFileExtention(String projectName) {
		 String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_STORY_FILE_EXTENTION.concat(projectName));
	     return  get(key);
	}
	

	public synchronized static String getStoryPathSystemProperty(String projectName) {
		 String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY.concat(projectName));
	     return  get(key);
	}

	public synchronized static String getAdditionalJvmOptions(String projectName) {
		 String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS.concat(projectName));
	     return  get(key);
	}
	
	public synchronized static String getJUnitVersion(String projectName) {
		 String key = ProjectPreferencePage.PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_JUNIT_VERSION.concat(projectName));
	     return  get(key);
	}
	

}
