# Eclipse JBehave JUnit Test Runner # ![](https://travis-ci.org/obeimnet/tech.idehub.eclipse.jbehave.svg)
This plugin allows running JBehave story files as JUnit tests using context menu from explorer view or an editor.

![](https://github.com/obeimnet/tech.idehub.eclipse.jbehave/blob/master/docs/images/run-from-context.png)
# Installation #

TODO

----------
# How it Works #

The plugin sets the selected story file (or folder containing story files) as a system property (default: jbehave.story.path) to a custom JBehave JUnit runner class.

- You will need to provide a custom JBehave JUnit runner class as in the following example:

**Example:** 
  <pre>
  @RunWith(......)
  public class MyStoryRunner extends JUnitStories {
   @Override
   protected List<String> storyPaths() {
	   List<String> stories = new ArrayList<>();
       stories.add(System.getProperty("jbehave.story.path"));
       return stories;
   }
 } </pre>

----------
# Configuring the Plugin #

- After installation, go to Windows -> Preferences page.

- On the Preferences page, locate 'JBehave JUnit Runner'.
![](https://github.com/obeimnet/tech.idehub.eclipse.jbehave/blob/master/docs/images/jbehave-junit-run-debug-configuration.png)
- Now you will need to enter yuor custom JBehave JUnit runner class.
![](https://github.com/obeimnet/tech.idehub.eclipse.jbehave/blob/master/docs/images/jbehave-junit-run-debug-configuration-2.png)

There are three options for Story Path Resolution Strategy.

- Default
- <pre>
  Passes story file name relative to moudle. 
  If the project has default maven settings, maven resource folders will be ignored.
  Example: For story file located in c:/myWorkspace/myProject1/src/test/resources/myStories/group1/blah.story,
           the plugin will set system property jbehave.story.path to "myStories/group1/blah.story" 
           
</pre>
- The other two options are self explanatory.

