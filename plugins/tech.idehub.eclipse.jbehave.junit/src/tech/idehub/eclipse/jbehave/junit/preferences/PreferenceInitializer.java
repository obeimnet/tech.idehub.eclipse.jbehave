package tech.idehub.eclipse.jbehave.junit.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import tech.idehub.eclipse.jbehave.junit.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	 
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_STORY_FILE_EXTENTION, ".story");
		store.setDefault(PreferenceConstants.P_RUNNER_CLASS, "");
		store.setDefault(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY, "jbehave.story.path");
		store.setDefault(PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY, PreferenceConstants.StoryNameResolverType.DEFAULT.name());
		store.setDefault(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS, "");
	}

}
