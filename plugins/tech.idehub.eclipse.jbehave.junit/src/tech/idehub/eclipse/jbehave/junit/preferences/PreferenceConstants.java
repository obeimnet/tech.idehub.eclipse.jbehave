package tech.idehub.eclipse.jbehave.junit.preferences;

/**
 * Constant definitions for plug-in preferences
 */

public class PreferenceConstants {
	
	public enum StoryNameResolverType {
		DEFAULT, PROJECT_RELATIVE, ABSOLUTE_PATH;
	}
	
	public static final String P_RUNNER_CLASS = "runnerClass";
	
	public static final String P_STORY_FILE_EXTENTION = "storyFileExtention";
	
	public static final String P_STORY_PATH_SYSTEM_PROPERTY = "storyPathSystemProperty";

	public static final String P_STORY_FILE_RESOLUTION_STRATEGY = "storyFileResolutionStrategy";
	
	public static final String P_ADDITIONAL_JVM_OPTIONS =  "additionalJvmOptions";
}
