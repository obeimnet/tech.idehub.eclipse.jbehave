
package tech.idehub.eclipse.jbehave.junit.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
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


	public JBehaveJUnitLaunchShortcut() {
	}


	public void launch(IEditorPart editor, String mode) {

		try {
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput() ;
		    IFile file = input.getFile();
		    StoryPath storyPath = ResourceNameResolver.resolve(file);
			performLaunch(getProjectName(editor), storyPath,  mode);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

	}

	public void launch(ISelection selection, String mode) {

		try {
			StoryPath storyPath = ResourceNameResolver.resolve(selection);
			performLaunch(getProjectName(selection), storyPath, mode);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	private void performLaunch(String projectName, StoryPath storyPath, String mode) throws InterruptedException, CoreException {
		ILaunchConfigurationWorkingCopy temparary= createLaunchConfiguration(projectName, storyPath);
		ILaunchConfiguration config= findExistingLaunchConfiguration(temparary, storyPath, mode);
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


	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(String projectName, StoryPath storyPath) throws CoreException {

		String mainTypeQualifiedName = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_RUNNER_CLASS);
		String storyPathSysProperty = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY);
		String additionalJvmOptions = JBehaveRunnerPreferenceCache.get(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS);

		final String testName = storyPath.displayName();
		final String containerHandleId = "";

		ILaunchConfigurationType configType= getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
		ILaunchConfigurationWorkingCopy wc= configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(testName));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeQualifiedName);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		wc.setAttribute(ATTR_KEEPRUNNING, false);
		wc.setAttribute(ATTR_TEST_CONTAINER, containerHandleId);
		wc.setAttribute(ATTR_TEST_RUNNER_KIND, JUNIT4_TEST_KIND_ID);
		String jvmArguments = "-D" + storyPathSysProperty + "=" + storyPath.jvmArgStoryPath() + " " + additionalJvmOptions;
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, jvmArguments);

		return wc;
	}


 	protected String[] getAttributeNamesToCompare() {
		return new String[] {
			IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
			IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
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


	private ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy temporary, StoryPath storyPath, String mode) throws InterruptedException, CoreException {
		List<ILaunchConfiguration> candidateConfigs= findExistingLaunchConfigurations(temporary, storyPath);

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

	private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary, StoryPath storyPath) throws CoreException {
		ILaunchConfigurationType configType= temporary.getType();

		ILaunchConfiguration[] configs= getLaunchManager().getLaunchConfigurations(configType);
		String[] attributeToCompare= getAttributeNamesToCompare();
		String candidateConfigurationName = storyPath.displayName();
		ArrayList<ILaunchConfiguration> candidateConfigs= new ArrayList<ILaunchConfiguration>(configs.length);
		for (ILaunchConfiguration config : configs) {

			if (hasSameAttributes(config, temporary, attributeToCompare) && config.getName().equals(candidateConfigurationName)) {
				candidateConfigs.add(config);
			}
		}
		return candidateConfigs;
	}



	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		//TODO
		return null;
	}


	public ILaunchConfiguration[] getLaunchConfigurations(final IEditorPart editor) {
		//TODO
		return null;
	}


	public IResource getLaunchableResource(ISelection selection) {
		//TODO
		return null;
	}


	public IResource getLaunchableResource(IEditorPart editor) {
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput() ;
	    IFile file = input.getFile();
		return file;
	}
}
