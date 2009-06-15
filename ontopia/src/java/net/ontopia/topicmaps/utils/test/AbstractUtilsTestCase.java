
// $Id: AbstractUtilsTestCase.java,v 1.14 2008/06/13 08:36:29 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.io.*;
import java.net.*;
import java.util.*;

import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.utils.*;


public class AbstractUtilsTestCase extends AbstractTopicMapTestCase {

  protected final static String FILE_SEPARATOR = System.getProperty("file.separator");

  protected LocatorIF baseAddress;
  protected TopicMapIF tm;


  public AbstractUtilsTestCase(String name) {
    super(name);
  }

  protected TopicMapIF getTopicMap() {
    return tm;
  }
  
  protected TopicIF getTopic(String fragId) {
    LocatorIF l = baseAddress.resolveAbsolute("#" + fragId);
    return (TopicIF) tm.getObjectByItemIdentifier(l);
  }

  protected void readFile(String fileName) {
    try {
      TopicMapReaderIF reader = ImportExportUtils.getReader(fileName);
      tm = reader.read();
      baseAddress = tm.getStore().getBaseAddress();
    } catch(IOException ex) {
      assertTrue("Topic map read failed!\n" + ex.getMessage(), false);
    }
  }
   
}
