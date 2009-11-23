package net.ontopia.topicmaps.utils.jtm.test;

import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapWriter;
import net.ontopia.topicmaps.utils.test.AbstractUtilsTestCase;

public class JTMTopicMapWriterTest extends AbstractUtilsTestCase {

  public JTMTopicMapWriterTest(String name) {
    super(name);
  }

  public void testConsole() throws IOException {
    readFile("./src/test-data/query/family.ltm");
    
    JTMTopicMapWriter writer = new JTMTopicMapWriter(System.out);
    //LocatorIF base = tm.getStore().getBaseAddress();
    //TMObjectIF obj = tm.getObjectByItemIdentifier(base.resolveAbsolute("#mother"));
    //writer.write(tm);
  }
}
