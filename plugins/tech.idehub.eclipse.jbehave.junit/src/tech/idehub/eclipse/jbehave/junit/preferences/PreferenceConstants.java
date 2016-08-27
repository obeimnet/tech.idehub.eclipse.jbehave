package tech.idehub.eclipse.jbehave.junit.preferences;

/**
 * Constant definitions for plug-in preferences
 */

public class PreferenceConstants {

	public enum StoryNameResolverType {

		DEFAULT("Default"),
		PROJECT_RELATIVE("Relative to Project Location"),
		ABSOLUTE_PATH("Absolute Path");
		public static final String[] descriptions = {DEFAULT.getDescription(),
				PROJECT_RELATIVE.getDescription(), ABSOLUTE_PATH.getDescription()};

		private final String description;
		private StoryNameResolverType(String description) {
			this.description =  description;
		}

		public String getDescription() {
			return description;
		}

		public static String[] descriptions() {
			return descriptions;
		}

		public static StoryNameResolverType byDescription(String description) {
			for (StoryNameResolverType snr : StoryNameResolverType.values()) {
				if (snr.description.equalsIgnoreCase(description)) {
					return snr;
				}
			}
			return StoryNameResolverType.DEFAULT;
		}
	}


	public static final String P_RUNNER_CLASS = "runnerClass";

	public static final String P_STORY_FILE_EXTENTION = "storyFileExtention";

	public static final String P_STORY_PATH_SYSTEM_PROPERTY = "storyPathSystemProperty";

	public static final String P_STORY_FILE_RESOLUTION_STRATEGY = "storyFileResolutionStrategy";

	public static final String P_ADDITIONAL_JVM_OPTIONS =  "additionalJvmOptions";
}
