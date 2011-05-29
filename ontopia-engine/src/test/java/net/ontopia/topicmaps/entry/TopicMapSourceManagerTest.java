
package net.ontopia.topicmaps.entry;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import junit.framework.TestCase;

public class TopicMapSourceManagerTest extends TestCase {
  TopicMapSourceManager manager;

  public TopicMapSourceManagerTest(String name) {
    super(name);
  }

  public void setUp() {
    manager = new TopicMapSourceManager();
  }
  
  // --- Test cases

  public void testEmpty() {
    verifyEmptyManager("");
    manager.refresh();
    verifyEmptyManager(" after refresh");
  }

  public void testSingleSourceSingleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    manager.addSource(source);
    manager.refresh();

    assertTrue("id collection of wrong size",
           manager.getIds().size() == 1);
    assertTrue("id of reference lost",
           manager.getIds().contains("id"));
                       
    assertTrue("reference not found",
           manager.getReferenceById("id") == ref);
    
    assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 1);
    assertTrue("reference identity lost",
           manager.getReferences().contains(ref));
    
    assertTrue("source collection of wrong size",
           manager.getSources().size() == 1);
    assertTrue("source lost",
           manager.getSources().contains(source));

    manager.removeSource(source);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  public void testSingleSourceDoubleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    DefaultTopicMapReference ref2 =
      new DefaultTopicMapReference("id2", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    source.addReference(ref2);
    manager.addSource(source);
    manager.refresh();

    assertTrue("id collection of wrong size",
           manager.getIds().size() == 2);
    assertTrue("id of reference lost",
           manager.getIds().contains("id") &&
           manager.getIds().contains("id2"));
    
    assertTrue("reference not found",
           manager.getReferenceById("id") == ref &&
           manager.getReferenceById("id2") == ref2 );
    
    assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 2);
    assertTrue("reference identity lost",
           manager.getReferences().contains(ref) &&
           manager.getReferences().contains(ref2));
    
    assertTrue("source collection of wrong size",
           manager.getSources().size() == 1);
    assertTrue("source lost",
           manager.getSources().contains(source));

    manager.removeSource(source);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  public void testDoubleSourceSingleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    manager.addSource(source);
    
    DefaultTopicMapSource source2 = new DefaultTopicMapSource();
    DefaultTopicMapReference ref2 =
      new DefaultTopicMapReference("id2", "title", new InMemoryTopicMapStore());
    source2.addReference(ref2);
    manager.addSource(source2);
    manager.refresh();
    
    assertTrue("id collection of wrong size",
           manager.getIds().size() == 2);
    assertTrue("id of reference lost",
           manager.getIds().contains("id") &&
           manager.getIds().contains("id2"));
    
    assertTrue("reference not found",
           manager.getReferenceById("id") == ref &&
           manager.getReferenceById("id2") == ref2 );
    
    assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 2);
    assertTrue("reference identity lost",
           manager.getReferences().contains(ref) &&
           manager.getReferences().contains(ref2));
    
    assertTrue("source collection of wrong size",
           manager.getSources().size() == 2);
    assertTrue("source lost",
           manager.getSources().contains(source) &&
           manager.getSources().contains(source2));

    manager.removeSource(source);
    manager.removeSource(source2);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  
  // --- INTERNAL METHODS

  private void verifyEmptyManager(String suffix) {
    assertTrue("id collection not empty" + suffix,
           manager.getIds().size() == 0);

    assertTrue("non-existent reference found" + suffix,
           manager.getReferenceById("rongobongo") == null);
    
    assertTrue("reference collection not empty" + suffix,
           manager.getReferences().size() == 0);
    
    assertTrue("source collection not empty" + suffix,
           manager.getSources().size() == 0);
  }
}







