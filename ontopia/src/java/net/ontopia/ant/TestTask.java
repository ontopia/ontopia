
// $Id: TestTask.java,v 1.4 2002/06/05 12:03:29 larsga Exp $

package net.ontopia.ant;

import net.ontopia.test.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class TestTask extends Task {
  protected String group;
  protected String file;

  public void init() {
    group = "default";
    if (System.getProperties().containsKey("test.group"))
      group = System.getProperty("test.group");
  }

  public void execute() throws BuildException {
    if (file == null)
      throw new BuildException("Attribute 'testfile' must be set!");

    TestRunner runner = new TestRunner();
    try {
      runner.run(file, group);
    }
    catch (java.io.IOException e) {
      throw new BuildException(e.toString());
    }
    catch (org.xml.sax.SAXException e) {
      throw new BuildException(e.toString());
    }
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public void setTestfile(String file) {
    this.file = file;
  }
}
