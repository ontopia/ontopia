
// $Id$

package net.ontopia.topicmaps.viz.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import net.ontopia.utils.StringUtils;
import net.ontopia.test.AbstractOntopiaTestCase;

public class ResourceBundlesTest extends AbstractOntopiaTestCase {

  public ResourceBundlesTest(String name) {
    super(name);
  }
  
  /**
   * Checks all properties files in a given directory for consistency
   * against the master file. Note that the properties are loaded from
   * the classpath.
   */
  public void testTranslationsAreConsistent() throws IOException {
    URL msgurl = getClass().getResource("/net/ontopia/topicmaps/viz");
    File msgdir = new File(msgurl.getFile());
    if (!msgdir.canRead() || !msgdir.isDirectory()) {
      fail("not a readable directory: " + msgdir);
    }
    
    Properties master = loadProperties(new File(msgdir, "messages.properties"));
    for (File file : matchFiles(msgdir, "messages_..\\.properties")) {
      if (!file.canRead() || !file.isFile()) {
        fail("not a readable file: " + file);
      }
      List missing = new ArrayList();
      List extra = new ArrayList();
      Properties trans = loadProperties(file);
      
      for (Object prop : trans.keySet()) {
        if (!master.containsKey(prop)) {
          extra.add(prop);
        }
      }
      
      for (Object prop : master.keySet()) {
        if (!trans.containsKey(prop)) {
          missing.add(prop);
        }
      }

      assertTrue(buildReport(file, missing, extra),
                 missing.isEmpty() && extra.isEmpty());
    }
  }

  private static Properties loadProperties(File file) throws IOException {
    Properties props = new Properties();
    FileInputStream inputStream = new FileInputStream(file);
    props.load(inputStream);
    inputStream.close();
    return props;
  }

  private static File[] matchFiles(File msgdir, final String filePattern) {
    return msgdir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.matches(filePattern);
      }
    });
  }
  
  /**
   * Builds a readable error message listing everything that's wrong.
   */
  private String buildReport(File file, List missing, List extra) {
    String msg = file.toString();
    if (!missing.isEmpty())
      msg += " is missing: " + StringUtils.join(missing, ", ");
    if (!extra.isEmpty())
      msg += " has extra: " + StringUtils.join(extra, ", ");
    return msg;
  }
}
