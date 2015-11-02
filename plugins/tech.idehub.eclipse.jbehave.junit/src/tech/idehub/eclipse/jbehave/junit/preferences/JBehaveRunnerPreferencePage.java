package tech.idehub.eclipse.jbehave.junit.preferences;

import java.util.Iterator;

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
		
		FieldEditor storyFileExtenstionField = new StringFieldEditor(PreferenceConstants.P_STORY_FILE_EXTENTION, "&JBehave Story File Extention (default = .story):", getFieldEditorParent());
		addField(storyFileExtenstionField);
		
		FieldEditor runnerClassField = new StringFieldEditor(PreferenceConstants.P_RUNNER_CLASS, "&JBehave JUnit Story Runner Class:", getFieldEditorParent());		
		addField(runnerClassField);
		
		
		FieldEditor storyPathSystemPropertyField = new StringFieldEditor(PreferenceConstants.P_STORY_PATH_SYSTEM_PROPERTY, "&Story Path System Property (default = jbehave.story.path):", getFieldEditorParent());
		addField(storyPathSystemPropertyField);
			 
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