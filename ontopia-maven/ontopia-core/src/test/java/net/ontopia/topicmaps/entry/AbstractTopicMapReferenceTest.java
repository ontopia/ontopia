
// $Id: AbstractTopicMapReferenceTest.java,v 1.7 2007/08/29 12:56:02 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import junit.framework.*;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

public abstract class AbstractTopicMapReferenceTest extends TestCase {

  public AbstractTopicMapReferenceTest(String name) {
    super(name);
  }

  // --- utility methods

  public void doAbstractTopicMapReferenceTests(AbstractTopicMapReference ref, 
                                               boolean checkOpenAfterClose) throws java.io.IOException {
    // WARNING: always run these tests as the last test as the
    // reference is being closed

    // test id
    String id = ref.getId();
    assertTrue("Id default is not set", ref.getId() != null);
    ref.setId("foo");
    assertTrue("Id not equals 'foo'", "foo".equals(ref.getId()));
    ref.setId(id);
    assertTrue("Id not equals '" + id + "'", id.equals(ref.getId()));
    
    // test title
    String title = ref.getTitle();
    assertTrue("Title default is not set", ref.getTitle() != null);
    ref.setTitle("foo");
    assertTrue("Title not equals 'foo'", "foo".equals(ref.getTitle()));
    ref.setTitle(title);
    assertTrue("Title not equals '" + title + "'", title.equals(ref.getTitle()));
    
    // test source
    TopicMapSourceIF source = ref.getSource();
    TopicMapSourceIF esource = new EmptyTopicMapSource("empty");
    ref.setSource(esource);
    assertTrue("Source != esource", esource == ref.getSource());
    ref.setSource(source);
    assertTrue("Source not equals '" + source + "'", source == ref.getSource());

    // test createStore
    TopicMapStoreIF store1 = ref.createStore(true);
    TopicMapStoreIF store2 = ref.createStore(false);

    // reference should be open after create
    assertTrue("Reference not open after createStore", ref.isOpen());

    assertTrue("store1 is null", store1 != null);
    assertTrue("store2 is null", store2 != null);
    
    // if reference has been closed then isOpen should return false
    ref.close();
    assertTrue("Reference open after close", !ref.isOpen());
    assertTrue("Reference deleted after close", !ref.isDeleted());

    // should not be possible to create store after close
    try {
      TopicMapStoreIF store = ref.createStore(true);
      assertTrue("Reference open after failed createStore", ref.isOpen());
    } catch (ReferenceNotOpenException e) {
      fail("Could not create store after reference " + ref + " had been closed.");
    }

    // store1 and store2 should also have been closed
    if (checkOpenAfterClose) {
      assertTrue("store1 open after reference close", store1.isOpen());
      assertTrue("store2 open after reference close", store2.isOpen());
    }

    // should not be possible to delete after close
    if (ref.getSource() != null) {
      try {
        ref.delete();
        assertTrue("Reference not deleted after delete", ref.isDeleted());
        assertTrue("Reference open after delete", !ref.isOpen());
      } catch (ReferenceNotOpenException e) {
        fail("Could not delete reference " + ref + " after close.");
      }
    }

  }

  /* -- tm source stub used for testing purposes -- */
  static class EmptyTopicMapSource implements TopicMapSourceIF {
    private String id;
    private String title;
    EmptyTopicMapSource(String id) { this.id = id; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Collection getReferences() { return Collections.EMPTY_SET; }
    public void refresh() {};    
    public boolean supportsCreate() {
      return false;
    }
    public boolean supportsDelete() {
      return false;
    }
    public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
      throw new UnsupportedOperationException();
    }
  }
}
