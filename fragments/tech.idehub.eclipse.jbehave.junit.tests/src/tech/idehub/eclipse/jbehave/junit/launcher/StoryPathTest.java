package tech.idehub.eclipse.jbehave.junit.launcher;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StoryPathTest {
	
	 @Test
	 public void test_display_folder_name() {
		 String folderName = "abc/def";
		 
		 StoryPath storyPath =  new StoryPath(folderName, true, ".story");
		 assertEquals("abc.def_", storyPath.displayName());
	 }
	 
	 @Test
	 public void test_display_file_name() {
		 String folderName = "abc/def/ghi.story";

		StoryPath storyPath =  new StoryPath(folderName, false, ".story");
		assertEquals("abc.def.ghi.story", storyPath.displayName());
	 }
}