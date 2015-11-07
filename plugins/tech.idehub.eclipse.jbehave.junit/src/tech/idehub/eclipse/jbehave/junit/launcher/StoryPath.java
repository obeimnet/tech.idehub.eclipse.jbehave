package tech.idehub.eclipse.jbehave.junit.launcher;


class StoryPath {
	
	private String path;
	private final boolean folder;
	private final String stoyFileExtention;
	
	StoryPath(String path, boolean folder, String stoyFileExtention) {
		this.path = path;
		this.folder = folder;
		this.stoyFileExtention = stoyFileExtention;
	}

	
	
	public void setPath(String path) {
		this.path = path;
	}


	public String getPath() {
		return this.path;
	}

	public boolean isFolder() {
		return this.folder;
	}

	public String displayName() {
		if (isFolder()) {
			return getPath().toLowerCase().replace('/', '.').concat("_");
		}

		return getPath().toLowerCase().replace('/', '.');
  }

  public String jvmArgStoryPath() {
	  if (isFolder()) {
			return getPath().concat("/**/*").concat(stoyFileExtention);
		}

		return getPath();
  }
}