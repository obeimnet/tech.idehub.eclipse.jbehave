package tech.idehub.eclipse.jbehave.junit.launcher;


import static tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache.getStoryFileExtention;
import static tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache.getStoryFileResolutionStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants;

//TODO investigate if maven nature could be used to find maven standard folders
class ResourceNameResolver {

	private static final String SRC_FOLDER = "src";
	private static final String SRC_MAIN_FOLDER = "src/main";
	private static final String SRC_MAIN_JAVA_FOLDER = "src/main/java";
	private static final String SRC_MAIN_RESOURCES_FOLDER = "src/main/resources";

	private static final String SRC_TEST_FOLDER = "src/test";
	private static final String SRC_TEST_JAVA_FOLDER = "src/test/java";
	private static final String SRC_TEST_RESOURCES_FOLDER = "src/test/resources";

	private static final List<String> MAVEN_RESOURCES;

	static {
		MAVEN_RESOURCES = new ArrayList<>();
		// keep insertion order
		MAVEN_RESOURCES.add(SRC_MAIN_RESOURCES_FOLDER);
		MAVEN_RESOURCES.add(SRC_MAIN_JAVA_FOLDER);
		MAVEN_RESOURCES.add(SRC_MAIN_FOLDER);
		MAVEN_RESOURCES.add(SRC_TEST_RESOURCES_FOLDER);
		MAVEN_RESOURCES.add(SRC_TEST_JAVA_FOLDER);
		MAVEN_RESOURCES.add(SRC_TEST_FOLDER);
		MAVEN_RESOURCES.add(SRC_FOLDER);

	}

	static StoryPath resolve(IResource resource) {

		IPath path = resource.getLocation();
		if (path == null) {
			return null;
		}
		
		String projectName = resource.getProject().getName();
		PreferenceConstants.StoryNameResolverType storyNameResolverType = PreferenceConstants.StoryNameResolverType
				.valueOf(getStoryFileResolutionStrategy(resource.getProject().getName()));
		
		StoryPath storyPath = null;
		switch (storyNameResolverType) {
		case ABSOLUTE_PATH:
			storyPath = resolveAbsolutePath(resource, projectName);
			break;
		case PROJECT_RELATIVE:
			storyPath = resolveProjectRelative(resource, projectName);
			break;
		case DEFAULT:
			storyPath = resolveDefault(resource, projectName);
		}

		if ((storyPath != null) && storyPath.isFolder()) {
			storyPath.setPath(storyPath.getPath());
		}

		//remove leading / from story path
		if ((storyPath != null)  && (storyPath.getPath().startsWith("/"))) {
			return new StoryPath(storyPath.getPath().substring(1), storyPath.isFolder(), getStoryFileExtention(resource.getProject().getName()));
		}


		return storyPath;
	}

	static StoryPath resolve(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IJavaElement) {
				IJavaElement element = (IJavaElement) obj;
				if (IJavaElement.PACKAGE_FRAGMENT  == element.getElementType())
					return resolve(element.getResource());
			} else {
				if (obj instanceof IResource) {
					IResource resource = (IResource) obj;
					return resolve(resource);
				}
				if (selection instanceof IJavaElement) {
					IJavaElement element = (IJavaElement) selection;
					if (IJavaElement.PACKAGE_FRAGMENT == element.getElementType())
						return resolve(element.getResource());
				}
			}
		}
		return null;
	}

	static StoryPath resolve(IJavaElement element) {
		if (IJavaElement.PACKAGE_FRAGMENT == element.getElementType()) {
			return resolve(element.getResource());
		}
		return null;
	}

	private static StoryPath resolveDefault(IResource resource, String projectName) {

		String resourceName = defaultStoryFilePath(resource);
		if (resourceName == null || resourceName.isEmpty()) {
			return null;
		}

		switch (resource.getType()) {
			case IResource.FILE:
				return new StoryPath(resourceName, false, getStoryFileExtention(projectName));
			case IResource.FOLDER:
				return new StoryPath(resourceName, true, getStoryFileExtention(projectName));
		}

		return null;
	}

	private static StoryPath resolveAbsolutePath(IResource resource, String projectName) {

		switch (resource.getType()) {
		case IResource.FILE:
			return new StoryPath(resource.getRawLocation().toString(), false, getStoryFileExtention(projectName));
		case IResource.FOLDER:
			return new StoryPath(resource.getRawLocation().toString(), true, getStoryFileExtention(projectName));
		}
		return null;
	}

	private static StoryPath resolveProjectRelative(IResource resource, String projectName) {
		switch (resource.getType()) {
		case IResource.FILE:
			return new StoryPath(resource.getProjectRelativePath().toString(), false, getStoryFileExtention(projectName));
		case IResource.FOLDER:
			return new StoryPath(resource.getProjectRelativePath().toString(), true, getStoryFileExtention(projectName));
		}
		return null;
	}

	private static Set<String> getExcludedFolders(String projectPath) {
        String moduleName_ = projectPath;
        if (projectPath.endsWith("/")) {
            moduleName_ = projectPath.substring(0, projectPath.length() - 1);
        }
        Set<String> set = new HashSet<>();
        for (String mavenResource : MAVEN_RESOURCES) {
            set.add(moduleName_.concat("/").concat(mavenResource));
        }
        return  set;
    }

	private static String defaultStoryFilePath(IResource resource) {

        String projectPath = resource.getProject().getLocation().toString();
        String resourcePath =   resource.getLocation().toString();

        Set<String> excludedFolders = getExcludedFolders(projectPath);
        if (excludedFolders.contains(resourcePath)) {
            return "";
        }

        resourcePath = resourcePath.replace(projectPath, "");
        for (String mavenResource : MAVEN_RESOURCES) {
            resourcePath = resourcePath.replace(mavenResource, "");
        }

        if (resourcePath.startsWith("/"))  {
            return resourcePath.substring(1);
        }
        return resourcePath;
    }
}