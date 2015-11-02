package tech.idehub.eclipse.jbehave.junit.launcher;

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

import tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache;
import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants;

public class JBehaveJUnitLaunchableTester extends PropertyTester {

	private static final String  PROP_CAN_LAUNCH_JBEHAVE = "canLaunchJBehave";
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		if (!(receiver instanceof IAdaptable)) {
            return false;
        }
		 
		if (PROP_CAN_LAUNCH_JBEHAVE.equals(property)) {
			return canLaunchJBehave(receiver);
		}
		return false;
	}

	private boolean canLaunchJBehave(Object receiver)  {
		
		  if (receiver instanceof IFileEditorInput) {
			  IFileEditorInput editor = (IFileEditorInput) receiver;
			  return canLaunchJBehaveResource(editor.getFile());	      			  
		  } else  if (receiver instanceof IStructuredSelection) {
	        IStructuredSelection ssel = (IStructuredSelection) receiver;
	        Object obj = ssel.getFirstElement();
	        if (obj instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) obj;				
				switch (element.getElementType()) {
					case IJavaElement.PACKAGE_FRAGMENT:
					  return canLaunchJBehaveResource(element.getResource());
					default:
						return false;
					
				}
			} 
		  } else if (receiver instanceof IResource) {
				IResource resource = (IResource) receiver;
	        	return canLaunchJBehaveResource(resource);	        	
	      } else if (receiver instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) receiver;				
				switch (element.getElementType()) {
					case IJavaElement.PACKAGE_FRAGMENT:
					  return canLaunchJBehaveResource(element.getResource());
					default:
						return false;
					
				}
		  }   

		return false;
	}
	
	private boolean canLaunchJBehaveResource(IResource resource)  {

		if (resource.isVirtual() || resource.isDerived() || resource.isLinked() || resource.isHidden()) {
			return false;
		}
		
		String storyFileExtention = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_STORY_FILE_EXTENTION);
				
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
