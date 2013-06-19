
package net.ontopia.topicmaps.impl.rdbms;

import junit.framework.TestCase;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class RDBMSTopicMapReferenceTest extends TestCase {

  public void testSetTitle() throws Exception {
    final String NEW_TITLE = "___NEW_TITLE___";

    RDBMSTestFactory factory = new RDBMSTestFactory();
    TopicMapReferenceIF before = factory.makeTopicMapReference();
    assertNotSame("Test reference already has title '" + NEW_TITLE + "', cannot test for change", NEW_TITLE, before.getTitle());
    before.setTitle(NEW_TITLE);
    String referenceId = before.getId();

    factory = new RDBMSTestFactory(); // reload
    TopicMapReferenceIF after = null;
    for (TopicMapReferenceIF i : factory.getSource().getReferences()) {
      if (referenceId.equals(i.getId())) { after = i; }
    }
    assertNotNull("Reference with id '" + referenceId + "' not found", after);
    assertEquals("Reference title not changed correctly", NEW_TITLE, after.getTitle());
  }

}
