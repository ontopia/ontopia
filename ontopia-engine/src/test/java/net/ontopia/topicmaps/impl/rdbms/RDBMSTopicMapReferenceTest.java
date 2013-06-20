
package net.ontopia.topicmaps.impl.rdbms;

import junit.framework.TestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
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

  public void testSetBaseAddress() throws Exception {
    final LocatorIF NEW_BASE_ADDRESS = URILocator.create("foo:bar-" + System.currentTimeMillis());

    RDBMSTestFactory factory = new RDBMSTestFactory();
    RDBMSTopicMapReference before = (RDBMSTopicMapReference) factory.makeTopicMapReference();
    assertNotSame("Test reference already has base address '" + NEW_BASE_ADDRESS.getAddress() + "', cannot test for change", NEW_BASE_ADDRESS, before.getBaseAddress());
    before.setBaseAddress(NEW_BASE_ADDRESS);
    String referenceId = before.getId();

    factory = new RDBMSTestFactory(); // reload
    RDBMSTopicMapReference after = null;
    for (TopicMapReferenceIF i : factory.getSource().getReferences()) {
      if (referenceId.equals(i.getId())) { after = (RDBMSTopicMapReference) i; }
    }
    assertNotNull("Reference with id '" + referenceId + "' not found", after);
    assertEquals("Reference base address not changed correctly", NEW_BASE_ADDRESS, after.getBaseAddress());
  }

}
