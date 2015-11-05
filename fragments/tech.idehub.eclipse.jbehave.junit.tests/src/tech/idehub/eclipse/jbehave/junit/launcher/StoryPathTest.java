package tech.idehub.eclipse.jbehave.junit.launcher;

import static java.io.File.separator;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StoryPathTest {
	
	 @Test
	 public void test_display_folder_name() {
		 String folderName = "abc".concat(separator)
				 			  .concat("def").concat(separator).concat("**")
				 			  .concat(separator).concat(".story");
		 
		 StoryPath storyPath =  new StoryPath(folderName, true);
		 assertEquals("abc.def.**.", storyPath.displayName());
	 }
	 
	 @Test
	 public void test_display_file_name() {
		 String folderName = "abc".concat(separator)
	 			  .concat("def").concat(separator)
	 			  .concat("ghi.story");

		StoryPath storyPath =  new StoryPath(folderName, false);
		assertEquals("abc.def.ghi.story", storyPath.displayName());
	 }
}