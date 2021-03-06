package tech.idehub.eclipse.jbehave.junit.launcher;

import static tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache.getStoryFileExtention;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;

import tech.idehub.eclipse.jbehave.junit.PluginConstants;

public class JBehaveJUnitLaunchableTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if (!(receiver instanceof IAdaptable)) {
            return false;
        }

		try {
			if (PluginConstants.PROP_CAN_LAUNCH_JBEHAVE.equals(property)) {
				return canLaunchJBehave(receiver);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

		return false;
	}

	protected boolean canLaunchJBehave(Object receiver)  {

		  if (receiver instanceof IFileEditorInput) {
			  IFileEditorInput editor = (IFileEditorInput) receiver;
			  return canLaunchJBehaveResource(editor.getFile());
		  } else  if (receiver instanceof IStructuredSelection) {
	        IStructuredSelection selection = (IStructuredSelection) receiver;
	        if (ResourceNameResolver.resolve(selection) != null) {
	        	return true;
	        }
		  } else if (receiver instanceof IResource) {
				IResource resource = (IResource) receiver;
	        	return canLaunchJBehaveResource(resource) && ResourceNameResolver.resolve(resource) != null ;
	      } else if (receiver instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) receiver;
				return canLaunchJBehaveResource(element.getResource()) && ResourceNameResolver.resolve(element) != null;
		  }

		return false;
	}

	private boolean canLaunchJBehaveResource(IResource resource)  {

		if (resource.isVirtual()
				|| resource.isDerived()
				|| resource.isLinked()
				|| resource.isHidden()
				//|| resource.getResourceAttributes().isArchive()
				|| !resource.isAccessible()) {
			return false;
		}

		String storyFileExtention = getStoryFileExtention(resource.getProject().getName());

		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (file.getName().endsWith(storyFileExtention)) {
				return true;
			}
		} else if (resource instanceof IFolder) {
			IFolder folder = (IFolder) resource;
			if (!folder.isDerived()) {
				try {
					 List<String> stories = new ArrayList<>();
					 containsStoryFile(folder, storyFileExtention, stories);
					 return !stories.isEmpty();
				} catch (CoreException exc) {
					exc.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	private void containsStoryFile(IFolder folder, String storyFileExtention, List<String> stories) throws CoreException {

		IResource[] members = folder.members();
		if (members == null || members.length == 0 || !stories.isEmpty()) {
			return;
		}

		for (IResource member : members) {
			if (member instanceof IFolder) {
				containsStoryFile(((IFolder) member), storyFileExtention, stories);
			} else if (member instanceof IFile) {
				IFile file = (IFile) member;
				if (file.getName().endsWith(storyFileExtention)) {
					stories.add(file.getName());
					break;
				}
			}
		}
	}

}
