
package tech.idehub.eclipse.jbehave.junit.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache;
import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants;


public class JBehaveJUnitLaunchShortcut implements ILaunchShortcut2 {

	private static final String EMPTY_STRING= ""; 
	
	private static final String JUNIT_PLUGIN_ID= "org.eclipse.jdt.junit"; 
	
	private static final String JUNIT4_TEST_KIND_ID = "org.eclipse.jdt.junit.loader.junit4";
	private static final String ATTR_TEST_RUNNER_KIND = JUNIT_PLUGIN_ID + ".TEST_KIND"; 
	private static final String ATTR_KEEPRUNNING = JUNIT_PLUGIN_ID + ".KEEPRUNNING_ATTR";
	private static final String ATTR_TEST_CONTAINER= JUNIT_PLUGIN_ID +".CONTAINER";
	private static final String ID_JUNIT_APPLICATION = "org.eclipse.jdt.junit.launchconfig"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public JBehaveJUnitLaunchShortcut() {
	}


	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.ui.IEditorPart, java.lang.String)
	 */
	public void launch(IEditorPart editor, String mode) {
		
		try {
			performLaunch(getProjectName(editor), getResourceLocation(editor),  mode);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		
	}

	 
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchShortcut#launch(org.eclipse.jface.viewers.ISelection, java.lang.String)
	 */
	public void launch(ISelection selection, String mode) {
		
		try {
			performLaunch(getProjectName(selection), getResourceLocation(selection), mode);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	private void performLaunch(String projectName, String storyPath, String mode) throws InterruptedException, CoreException {
		ILaunchConfigurationWorkingCopy temparary= createLaunchConfiguration(projectName, storyPath);
		ILaunchConfiguration config= findExistingLaunchConfiguration(temparary, mode);
		if (config == null) {
			// no existing found: create a new one
			config= temparary.doSave();
		}
		DebugUITools.launch(config, mode);
	}


	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
	

	protected String getLaunchConfigurationTypeId() {
		return ID_JUNIT_APPLICATION;
	}


	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(String projectName, String storyPath) throws CoreException {
		    
		String mainTypeQualifiedName = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_RUNNER_CLASS);
		String storyPathSysProperty = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY);
		
		final String testName = storyPath.toLowerCase().replace(File.pathSeparatorChar, '_');
		final String containerHandleId = "";

		ILaunchConfigurationType configType= getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
		ILaunchConfigurationWorkingCopy wc= configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(testName));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeQualifiedName);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		wc.setAttribute(ATTR_KEEPRUNNING, false);
		wc.setAttribute(ATTR_TEST_CONTAINER, containerHandleId);
		wc.setAttribute(ATTR_TEST_RUNNER_KIND, JUNIT4_TEST_KIND_ID);
		//JUnitMigrationDelegate.mapResources(wc);
		//AssertionVMArg.setArgDefault(wc);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-D" + storyPathSysProperty + "=" + storyPath);
		//if (element instanceof IMethod) {
			//wc.setAttribute(JUnitLaunchConfigurationConstants.ATTR_TEST_METHOD_NAME, element.getElementName()); // only set for methods
		//}
		return wc;
	}
 
	
 	protected String[] getAttributeNamesToCompare() {
		return new String[] {
			IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
			IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
			IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS
		};
	}

	private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
		try {
						
			for (String element : attributeToCompare) {
				String val1= config1.getAttribute(element, EMPTY_STRING);
				String val2= config2.getAttribute(element, EMPTY_STRING);
				if (!val1.equals(val2)) {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
			// ignore access problems here, return false
		}
		return false;
	}

	private String getProjectName(IEditorPart element) {
		IFileEditorInput input = (IFileEditorInput) element.getEditorInput() ;
	    IFile file = input.getFile();
	   
	    IProject project = file.getProject();
	    return project.getName();
	}
	
	private String getResourceLocation(IEditorPart element) {
		IFileEditorInput input = (IFileEditorInput) element.getEditorInput() ;
	    IFile file = input.getFile();
		return file.getProjectRelativePath().toString().replace("src/test/resources", "");
	}
	
	private String getProjectName(ISelection selection) {
		
		if (selection instanceof IStructuredSelection) {
	        IStructuredSelection ssel = (IStructuredSelection) selection;
	        Object obj = ssel.getFirstElement();
	        if (obj instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) obj;
				return element.getJavaProject().getProject().getName();
	        } else if (obj instanceof IResource) {
	        	IResource resource = (IResource) obj;
	        	return resource.getProject().getName();
	        } 
	        
	    }
		return null;
	}
	
	private String getResourceLocation(ISelection selection) throws CoreException {
		if (selection instanceof IStructuredSelection) {
	        IStructuredSelection ssel = (IStructuredSelection) selection;
	        Object obj = ssel.getFirstElement();
	        if (obj instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) obj;
				if (IJavaElement.PACKAGE_FRAGMENT == element.getElementType()) {
					return element.getResource().getProjectRelativePath().toString().replace("src/test/resources", "").concat("/**/*.story");
				}
			} else if (obj instanceof IResource) {
	        	IResource resource = (IResource) obj;
	        	if (IResource.FILE == resource.getType()) {
	        		return resource.getProjectRelativePath().toString().replace("src/test/resources", "");
	        	} else if (IResource.FOLDER == resource.getType()) {
	        		return resource.getProjectRelativePath().toString().replace("src/test/resources", "").concat("/**/*.story");
	        	}
	        	
	        }
	    }
		return null;
	}
	
	
	private ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy temporary, String mode) throws InterruptedException, CoreException {
		List<ILaunchConfiguration> candidateConfigs= findExistingLaunchConfigurations(temporary);

		// If there are no existing configs associated with the IType, create
		// one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the
		// IType, prompt the
		// user to choose one.
		int candidateCount= candidateConfigs.size();
		if (candidateCount == 0) {
			return null;
		} else if (candidateCount == 1) {
			return candidateConfigs.get(0);
		} else {
			//TODO: error
		}
		return null;
	}

	private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
		ILaunchConfigurationType configType= temporary.getType();

		ILaunchConfiguration[] configs= getLaunchManager().getLaunchConfigurations(configType);
		String[] attributeToCompare= getAttributeNamesToCompare();

		ArrayList<ILaunchConfiguration> candidateConfigs= new ArrayList<ILaunchConfiguration>(configs.length);
		for (ILaunchConfiguration config : configs) {
			
			if (hasSameAttributes(config, temporary, attributeToCompare)) {
				candidateConfigs.add(config);
			}
		}
		return candidateConfigs;
	}
	

	
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss= (IStructuredSelection) selection;
			if (ss.size() == 1) {
				return null; // findExistingLaunchConfigurations(ss.getFirstElement());
			}
		}
		return null;
	}

	
	public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editor) {
		//TODO
		return null;
	}

	
	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss= (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object selected= ss.getFirstElement();
				if (!(selected instanceof IJavaElement) && selected instanceof IAdaptable) {
					selected= ((IAdaptable) selected).getAdapter(IJavaElement.class);
				}
				if (selected instanceof IJavaElement) {
					return ((IJavaElement)selected).getResource();
				}
			}
		}
		return null;
	}

	
	public IResource getLaunchableResource(IEditorPart editor) {
		ITypeRoot element= JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		if (element != null) {
			try {
				return element.getCorrespondingResource();
			} catch (JavaModelException e) {
			}
		}
		return null;
	}
}
