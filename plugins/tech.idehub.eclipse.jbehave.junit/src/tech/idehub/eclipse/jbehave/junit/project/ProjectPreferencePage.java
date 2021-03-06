package tech.idehub.eclipse.jbehave.junit.project;

import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_RUNNER_CLASS;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_EXTENTION;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_JUNIT_VERSION;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import tech.idehub.eclipse.jbehave.junit.Activator;
import tech.idehub.eclipse.jbehave.junit.preferences.JBehaveRunnerPreferenceCache;
import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants;
import tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.StoryNameResolverType;

public class ProjectPreferencePage extends PropertyPage implements IWorkbenchPropertyPage {

	public static final String PREFERENCE_KEY_PREFIX = Activator.PLUGIN_ID.concat(Activator.VERSION);
	
	private static final String DEFAULT_VALUE_JUNIT_VERSION = PreferenceConstants.JUNIT4;
	private static final String DEFAULT_VALUE_STORY_PATH_STRATEGY = StoryNameResolverType.DEFAULT.name();
	private static final String DEFAULT_VALUE_STORY_FILE_SYS_PROP = "story.path";
	private static final String DEFAULT_VALUE_STORY_FILE_EXT = ".story";
	
	
	private String projectName;
	private Composite parent;
	
	StringFieldEditor runnerClassField;
	StringFieldEditor storyFileExtenstionField;
	StringFieldEditor storyPathSystemPropertyField;
	RadioGroupFieldEditor storyFileResolutionStrategyField;
	StringFieldEditor additionalJvmOptionsField;
	RadioGroupFieldEditor junitVersionRadioGroup;


	@Override
	protected Control createContents(Composite parent) {
		IAdaptable adaptable = getElement();
		if (adaptable.getAdapter(IProject.class) != null) {
			projectName = adaptable.getAdapter(IProject.class).getName();
		    return createTable(parent);
		}
		return null;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void performApply() {
		performOk();
	}

	@Override
	public boolean performOk() {
		JBehaveRunnerPreferenceCache.purge();
		
		//Runner class
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_RUNNER_CLASS).concat(projectName), runnerClassField.getStringValue());
		//Story File Extension
		String fileExt = storyFileExtenstionField.getStringValue();
		if (fileExt == null || fileExt.trim().length() == 0) {
			fileExt = DEFAULT_VALUE_STORY_FILE_EXT;
		} else {
			
		}		
		//Story Path System Property
		String storyPathSysProp =  storyPathSystemPropertyField.getStringValue();
		if (storyPathSysProp == null || storyPathSysProp.trim().length() == 0) {
			storyPathSysProp = DEFAULT_VALUE_STORY_FILE_SYS_PROP;
		} 
        String storyPathStrategy = getSelectedVaue(storyFileResolutionStrategyField, StoryNameResolverType.DEFAULT.name());	
		String junitVersion = getSelectedVaue(junitVersionRadioGroup, DEFAULT_VALUE_JUNIT_VERSION);
		
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_EXTENTION).concat(projectName), fileExt);
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_PATH_SYSTEM_PROPERTY).concat(projectName), storyPathSysProp);
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_RESOLUTION_STRATEGY).concat(projectName), storyPathStrategy);
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_ADDITIONAL_JVM_OPTIONS).concat(projectName), additionalJvmOptionsField.getStringValue());
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_JUNIT_VERSION).concat(projectName), junitVersion);
		return true;
	}

	@Override
	protected void performDefaults() {
		//updateApplyButton();
	}

	private Control createTable(Composite parent) {
		this.parent = parent;

		runnerClassField = new StringFieldEditor(PreferenceConstants.P_RUNNER_CLASS.concat(projectName), "&JBehave JUnit Story Runner Class:", parent);
		String runnerClass = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_RUNNER_CLASS).concat(projectName));
		runnerClassField.setStringValue(runnerClass);

		storyFileExtenstionField = new StringFieldEditor(PreferenceConstants.P_STORY_FILE_EXTENTION.concat(projectName), "&JBehave Story File Extention (default = .story):", parent);
		String storyFileExtenstion = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_EXTENTION).concat(projectName));
		storyFileExtenstionField.setStringValue(storyFileExtenstion);

		//--------------------------------------------------------------------------------------------------------------------------------------
		storyPathSystemPropertyField = new StringFieldEditor(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY.concat(projectName), "&Story Path System Property (default = story.path):", parent);
		String storyPathSystemProperty = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_STORY_PATH_SYSTEM_PROPERTY).concat(projectName));
		storyPathSystemPropertyField.setStringValue(storyPathSystemProperty);
		//--------------------------------------------------------------------------------------------------------------------------------------
		String[][] labelAndValues = {
				{"Default", "DEFAULT"}
				,{"Relative to Project Location", "PROJECT_RELATIVE"}
				,{"Absolute Path", "ABSOLUTE_PATH"}
		};

		storyFileResolutionStrategyField = new RadioGroupFieldEditor(PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY.concat(projectName),
														"&Story Path Resolution Strategy",
														1,
														labelAndValues, parent);

		String storyFileResolutionStrategy = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY).concat(projectName));
		if (storyFileResolutionStrategy == null || storyFileResolutionStrategy.trim().length() == 0) {
			storyFileResolutionStrategy = DEFAULT_VALUE_STORY_PATH_STRATEGY;
		}
		selectDefaultValue(storyFileResolutionStrategyField.getRadioBoxControl(parent), storyFileResolutionStrategy);
		//--------------------------------------------------------------------------------------------------------------------------------------
		additionalJvmOptionsField = new StringFieldEditor(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS.concat(projectName), "&Additional jvm options:", parent);
		String additionalJvmOptions = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_ADDITIONAL_JVM_OPTIONS).concat(projectName));
		additionalJvmOptionsField.setStringValue(additionalJvmOptions);
		//--------------------------------------------------------------------------------------------------------------------------------------
		//JUnit Version
		String[][] junitVersions = {
				{"JUnit 4", "JUnit4"}
				,{"JUnit 5", "JUnit5"}
		};
		junitVersionRadioGroup = new RadioGroupFieldEditor(PreferenceConstants.P_JUNIT_VERSION.concat(projectName),
				"&JUnit Version",
				1,
				junitVersions, parent);
		String junitVersion = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(PreferenceConstants.P_JUNIT_VERSION).concat(projectName));
		if (junitVersion == null || junitVersion.trim().length() == 0) {
			junitVersion = DEFAULT_VALUE_JUNIT_VERSION;
		}		
		selectDefaultValue(junitVersionRadioGroup.getRadioBoxControl(parent), junitVersion);
		
		return new Composite(parent, SWT.NULL);
	}
	
	private void selectDefaultValue(Composite composite, String defaultValue) {
		 Control[] resolutionStrategyBoxChildren = composite.getChildren();
		 for(Control child : resolutionStrategyBoxChildren)
	        {
	            if(child instanceof Button)
	            {
	           	 Button button = (Button)child;
	           	 if (button.getData().toString().equals(defaultValue)) {
	           		button.setSelection(true);
	           		break;
	           	 }
	            }
	        }
	}
	
	private String getSelectedVaue(RadioGroupFieldEditor radioGroup, String defaultValue) {
		String selectedValue = defaultValue;
		Composite temp = radioGroup.getRadioBoxControl(parent);
        Control[] children = temp.getChildren();
        for(Control child : children)
        {
            if(child instanceof Button)
            {
           	 Button button = (Button)child;
           	 if (button.getSelection()) {
           		selectedValue =  button.getData().toString();
           	 }
            }
        }
		return selectedValue;
	}
}
