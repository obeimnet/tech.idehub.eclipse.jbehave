package tech.idehub.eclipse.jbehave.junit.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import tech.idehub.eclipse.jbehave.junit.Activator;



public class JBehaveRunnerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public JBehaveRunnerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("");
	}

	
	public void createFieldEditors() {
		
		FieldEditor runnerClassField = new StringFieldEditor(PreferenceConstants.P_RUNNER_CLASS, "&JBehave JUnit Story Runner Class:", getFieldEditorParent());		
		addField(runnerClassField);
		
		FieldEditor storyFileExtenstionField = new StringFieldEditor(PreferenceConstants.P_STORY_FILE_EXTENTION, "&JBehave Story File Extention (default = .story):", getFieldEditorParent());
		addField(storyFileExtenstionField);
		
		FieldEditor storyPathSystemPropertyField = new StringFieldEditor(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY, "&Story Path System Property (default = jbehave.story.path):", getFieldEditorParent());
		addField(storyPathSystemPropertyField);
		
		String[][] labelAndValues = {
				{"Default", "DEFAULT"}
				,{"Relative to Project Location", "PROJECT_RELATIVE"}
				,{"Absolute Path", "ABSOLUTE_PATH"}
		};
		
		FieldEditor storyFileResolutionStrategyField = new RadioGroupFieldEditor(PreferenceConstants.P_STORY_FILE_RESOLUTION_STRATEGY, 
														"&Story Path Resolution Strategy",
														1,
														labelAndValues, getFieldEditorParent());
		addField(storyFileResolutionStrategyField);	 
		
		
		FieldEditor additionalJvmOptionsField = new StringFieldEditor(PreferenceConstants.P_ADDITIONAL_JVM_OPTIONS, "&Additional jvm options:", getFieldEditorParent());
		addField(additionalJvmOptionsField);
		
	}
	
	@Override
	public boolean performOk() {
		JBehaveRunnerPreferenceCache.purge();
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		JBehaveRunnerPreferenceCache.purge();
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}