package tech.idehub.eclipse.jbehave.junit.launcher;


import java.io.File;

class StoryPath {
	
	private final String path;
	private final boolean folder;

	StoryPath(String path, boolean folder) {
		this.path = path;
		this.folder = folder;
	}

	public String getPath() {
		return this.path;
	}

	public boolean isFolder() {
		return this.folder;
	}

	public String displayName() {
		if (isFolder()) {
			String folderName = getPath();
			int lastPeriodIndex = getPath().lastIndexOf(".");
			if (lastPeriodIndex != -1) {
				folderName = folderName.substring(0, lastPeriodIndex);
			}
			return folderName.toLowerCase().replace(File.separatorChar, '.');
		}
		return getPath().toLowerCase().replace(File.separatorChar, '.');
	}
}