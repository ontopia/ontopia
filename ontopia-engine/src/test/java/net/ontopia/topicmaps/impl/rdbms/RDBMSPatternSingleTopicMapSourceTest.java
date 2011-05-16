
package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class RDBMSPatternSingleTopicMapSourceTest
  extends AbstractTopicMapSourceTest {

  public RDBMSPatternSingleTopicMapSourceTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  // --- Test cases

  public void testSource() {
    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(makeSource());
  }

  public void testRefresh() throws IOException {
    // constants
    final String title = "test-topic-map";
    final String propfile = System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile");

    RDBMSTopicMapStore store = null;
    RDBMSTopicMapStore store2 = null;
    try {
      // create a new topic map matching pattern
      store = new RDBMSTopicMapStore(propfile);
      ((TopicMap) store.getTopicMap()).setTitle(title);
      store.commit(); // make sure it's in the DB for real
      
      // verify that correct TM is being picked up
      RDBMSPatternSingleTopicMapSource source = makeSource();
      source.setMatch("title");
      source.setPattern(title);
      TopicMapReferenceIF ref = (TopicMapReferenceIF)
        source.getReferences().iterator().next();
      assertTrue("reference has wrong ID: " + ref,
                 ((RDBMSTopicMapReference) ref).getTopicMapId() == store.getLongId());

      // make another topic map matching pattern
      store2 = new RDBMSTopicMapStore(propfile);
      ((TopicMap) store2.getTopicMap()).setTitle(title);
      store2.commit(); // make sure it's in the DB for real

      // verify that new topic map is being picked up
      source.refresh();
      ref = (TopicMapReferenceIF) source.getReferences().iterator().next();
      assertTrue("reference has wrong ID: " + ref,
                 ((RDBMSTopicMapReference) ref).getTopicMapId() == store2.getLongId());
    } finally {
      // clean up
      if (store != null)
        store.delete(true);
      if (store2 != null)
        store2.delete(true);
    }
  }

  // --- Internal methods

  private RDBMSPatternSingleTopicMapSource makeSource() {
    RDBMSPatternSingleTopicMapSource source = new RDBMSPatternSingleTopicMapSource();
    source.setId("foosource");
    source.setTitle("footitle");
    source.setReferenceId("foo");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setMatch("comments");
    source.setPattern("My comment");
    return source;
  }
  
}
