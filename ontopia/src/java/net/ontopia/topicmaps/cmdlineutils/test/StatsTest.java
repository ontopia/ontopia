// $Id: StatsTest.java,v 1.11 2002/05/29 13:38:38 hca Exp $

package net.ontopia.topicmaps.cmdlineutils.test;

import junit.framework.*;
import net.ontopia.topicmaps.cmdlineutils.*;
import net.ontopia.topicmaps.cmdlineutils.sanity.*;
import net.ontopia.topicmaps.cmdlineutils.statistics.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.infoset.impl.basic.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;

public class StatsTest extends CommandLineUtilsTest {
  
  public StatsTest(String name) {
    super(name);
  }

  protected void setUp() {
    
    XTMTopicMapReader reader  = null;

    String root = getTestDirectory();
    String filename = root + File.separator + "various" + File.separator + "stats.xtm";

    try {
      reader = new XTMTopicMapReader(new File(filename));
      tm = reader.read();
    } catch (IOException e) {
      fail("Error reading file\n" + e);
    }

  }

  protected void tearDown() {
    tm = null;
  }
  
  

  public static Test suite() {

    TestSuite suite = new TestSuite(StatsTest.class);
    
    return suite;
  }


}





