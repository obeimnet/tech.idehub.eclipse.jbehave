package tech.idehub.eclipse.jbehave.junit.launcher;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache;
import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants;

class ResourceNameResolver {
	
	static String resolve(IResource resource) {
		PreferenceConstants.StoryNameResolverType storyNameResolverType = PreferenceConstants.StoryNameResolverType
				.valueOf(JBehaveRunnerPreferenceCache.get("storyFileResolutionStrategy"));
		
		String retainLeadingSlash = JBehaveRunnerPreferenceCache.get("storyPathWithLeadingSlash");

		String storyPath = null;
		switch (storyNameResolverType) {
		case ABSOLUTE_PATH:
			storyPath = resolveAbsolutePath(resource);
			break;
		case PROJECT_RELATIVE:
			storyPath = resolveProjectRelative(resource);
			break;
		case DEFAULT:
			storyPath = resolveDefault(resource);
		}

		if ((storyPath != null) && ("false".equalsIgnoreCase(retainLeadingSlash)) && (storyPath.startsWith("/"))) {
			return storyPath.substring(1);
		}

		return storyPath;
	}

	static String resolve(ISelection selection) {
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

	static String resolve(IJavaElement element) {
		if (IJavaElement.PACKAGE_FRAGMENT == element.getElementType()) {
			return resolve(element.getResource());
		}
		return null;
	}

	private static String resolveDefault(IResource resource) {
		
		if ((isFolder(resource)) && (resource.getName().equals("resources"))) {
			IContainer parent = resource.getParent();
			if ((parent != null) 
					&& (((parent.getName().equals("test")) || (parent.getName().equals("java")))) 
					&& (parent.getParent() != null)
					&& (parent.getParent().getName().equals("src"))) {
				return null;
			}
		} else if ((isFolder(resource)) && (((resource.getName().equals("main")) || (resource.getName().equals("test"))))) {
			IContainer parent = resource.getParent();
			if (parent != null && parent.getName().equals("src")) {
				if (parent.getParent() != null && parent.getParent().getName().equals(resource.getProject().getName())) {
				return null;
				}
			}
		} else if (isFolder(resource) && resource.getName().equals("src")) {
			IContainer parent = resource.getParent();
			if (parent.getParent() != null && parent.getName().equals(resource.getProject().getName())) {
				return null;
			}
		}

		String resourceName = resource.getProjectRelativePath().toOSString().replaceFirst("src/test/resources", "").replaceFirst("src/test/java", "")
				.replaceFirst("src/main/resources", "").replaceFirst("src/main/java", "");
		switch (resource.getType()) {
		case IResource.FILE:
			return resourceName;
		case IResource.FOLDER:
			return resourceName.concat("/**/*.story");
		}
		return null;
	}

	private static String resolveAbsolutePath(IResource resource) {
		switch (resource.getType()) {
		case IResource.FILE:
			return resource.getRawLocation().toString();
		case IResource.FOLDER:
			return resource.getRawLocation().toString().concat("/**/*.story");
		}
		return null;
	}

	private static String resolveProjectRelative(IResource resource) {
		switch (resource.getType()) {
		case IResource.FILE:
			return resource.getProjectRelativePath().toString();
		case IResource.FOLDER:
			return resource.getProjectRelativePath().toString().concat("/**/*.story");
		}
		return null;
	}

	private static boolean isFolder(IResource resource) {
		return (IResource.FOLDER == resource.getType());
	}
}