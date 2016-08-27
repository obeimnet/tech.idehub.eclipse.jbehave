package tech.idehub.eclipse.jbehave.junit.project;

import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_RUNNER_CLASS;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_EXTENTION;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY;
import static tech.idehub.eclipse.jbehave.junit.preferences.PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY;

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

	private IProject project;
	private String projectName;
	private Composite parent;
	public static final String PREFERENCE_KEY_PREFIX = Activator.PLUGIN_ID.concat(Activator.VERSION);

	StringFieldEditor runnerClassField;
	StringFieldEditor storyFileExtenstionField;
	StringFieldEditor storyPathSystemPropertyField;
	RadioGroupFieldEditor storyFileResolutionStrategyField;
	StringFieldEditor additionalJvmOptionsField;


	@Override
	protected Control createContents(Composite parent) {
		IAdaptable adaptable = getElement();
		if (adaptable.getAdapter(IProject.class) != null) {
			project = (IProject) adaptable;
			projectName = project.getName();
		    return createTable(parent, project);
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
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_RUNNER_CLASS).concat(projectName), runnerClassField.getStringValue());
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_EXTENTION).concat(projectName), storyFileExtenstionField.getStringValue());
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_PATH_SYSTEM_PROPERTY).concat(projectName), storyPathSystemPropertyField.getStringValue());

		 Composite temp = storyFileResolutionStrategyField.getRadioBoxControl(parent);
         Control[] children = temp.getChildren();
         String storyPathStrategy = StoryNameResolverType.DEFAULT.name();
         for(Control child : children)
         {
             if(child instanceof Button)
             {
            	 Button button = (Button)child;
            	 if (button.getSelection()) {
            		 storyPathStrategy = StoryNameResolverType.valueOf(button.getData().toString()).name();
            	 }
             }
         }
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_RESOLUTION_STRATEGY).concat(projectName), storyPathStrategy);
		getPreferenceStore().setValue(PREFERENCE_KEY_PREFIX.concat(P_ADDITIONAL_JVM_OPTIONS).concat(projectName), additionalJvmOptionsField.getStringValue());
		return true;
	}

	@Override
	protected void performDefaults() {
		//updateApplyButton();
	}

	private Control createTable(Composite parent, IProject project) {
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

		String storyFileResolutionStrategy = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_STORY_FILE_RESOLUTION_STRATEGY).concat(projectName));
		if (storyFileResolutionStrategy == null || storyFileResolutionStrategy.trim().length() == 0) {
			storyFileResolutionStrategy = StoryNameResolverType.DEFAULT.name();
		}
		Composite radioButton = storyFileResolutionStrategyField.getRadioBoxControl(parent);
        Control[] children = radioButton.getChildren();
        for(Control child : children)
        {
            if(child instanceof Button)
            {
           	 Button button = (Button)child;
           	 if (button.getData().toString().equals(storyFileResolutionStrategy)) {
           		button.setSelection(true);
           		break;
           	 }
            }
        }
		storyPathSystemPropertyField.setStringValue(storyPathSystemProperty);

		//--------------------------------------------------------------------------------------------------------------------------------------
		additionalJvmOptionsField = new StringFieldEditor(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS.concat(projectName), "&Additional jvm options:", parent);
		String additionalJvmOptions = getPreferenceStore().getString(PREFERENCE_KEY_PREFIX.concat(P_ADDITIONAL_JVM_OPTIONS).concat(projectName));
		additionalJvmOptionsField.setStringValue(additionalJvmOptions);


		return new Composite(parent, SWT.NULL);
	}
}
