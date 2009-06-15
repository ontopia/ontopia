// $Id: PackageTest.java,v 1.41 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic.test;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import junit.framework.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.xml.*;

public class PackageTest extends net.ontopia.topicmaps.impl.basic.test.TopicMapPackageTest {
  
  public PackageTest(String name) {
    super(name);
  }

  protected void setUp() {
    if (tm == null) {
      try {
        TopicMapReaderIF reader =
          new XTMTopicMapReader(new java.io.File(getTestDirectory() + File.separator + "various" + File.separator + "package-test.xtm").toURL().toString());
        tm = reader.read();
        // base = (LocatorIF) tm.getItemIdentifiers().iterator().next();
        base = tm.getStore().getBaseAddress();
      }
      catch (java.io.IOException e) {
        e.printStackTrace();
        throw new RuntimeException("IMPOSSIBLE ERROR! " + e.getMessage());
      }
    }
  }

  protected void tearDown() {
    //tm = null;
  }
  
  public static Test suite() {
    return new TestSuite(PackageTest.class);
  }
    
}





