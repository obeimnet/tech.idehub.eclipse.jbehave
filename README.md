# Eclipse JBehave JUnit Story Runner #

[![Build Status](https://travis-ci.org/obeimnet/tech.idehub.eclipse.jbehave.svg?branch=master)](https://travis-ci.org/obeimnet/tech.idehub.eclipse.jbehave)

This plugin allows running JBehave story files as JUnit tests using context menu from project explorer view or an editor.

This documentation assumes the reader is a seasoned Eclipse user who knows how to install plugins.
If not, please help your self. Google it.

## Features ##

- The plugin is integrated with Eclipse's Run As (or Debug As) context menus.
- The context menu appears automatically when right clicking on: <br/>
&nbsp; * A folder containing one or more story files (on project explorer view) <br/>
&nbsp; * A story file (on project explorer view)<br/>
&nbsp; * A story editor

![](docs/images/run-from-context.png)

##  Installation ##
[Get](https://github.com/obeimnet/releases/blob/master/tech.idehub.eclipse.jbehave.update-site-1.0.0.RC1.zip) a zipped file and install it into eclipse from a local update archive.

## How it Works ##

- The plugin sets the path of selected story file (or folder containing story files) as a system property (default: story.path).
- A custom JBehave JUnit runner class reads the system property.
- You will need to provide a custom JBehave JUnit runner class as in the following example:

**Example:**
<pre>
  import org.jbehave.core.junit.JUnitStories
  @RunWith(......)
  public class MyStoryRunner extends JUnitStories {
   @Override
   protected List&lt;String&gt; storyPaths() {
	   List&lt;String&gt; stories = new ArrayList&lt;&gt;();
       stories.add(System.getProperty("story.path"));
       return stories;
   }
 }
</pre>

## Configuring the Plugin ##

- After installation, you have the option of setting preferences at two scopes:
- Workspace scope (applies to all projects) go to Windows -> Preferences page.
- Project scope, use project properties window

- On the Preferences (or project properties) page, locate 'JBehave JUnit Runner'.
![](docs/images/jbehave-junit-run-debug-configuration.png)
- Now you will need to enter yuor custom JBehave JUnit runner class.
![](docs/images/jbehave-junit-run-debug-configuration-2.png)

- There are three options for Story Path Resolution Strategy.
- Using example story file  located in *c:/myWorkspace/myProject1/src/test/resources/myStories/group1/blah.story* :
- **Default**
  - Passes story file name relative to project location.
  - In addition, if the project has default maven settings, maven resource folders will be ignored.
  -  story.path will be set to "myStories/group1/blah.story"
- **Relative to Project Location**
   - story.path will be set to "myStories/group1/blah.story"
- **Absolute Path**
   -  story.path will be set to "c:/myWorkspace/myProject1/src/test/resources/myStories/group1/blah.story"

**Additional jvm options**
- Allows you to pass extra jvm parameters, e.g. jvm heap parameters.

## Supported Platforms ##

The plugin has been testes in the following platforms:

- Java 7
- Eclipse Mars (4.5)
- Windows 7
