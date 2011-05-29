
package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.basic.index.IndexManager;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.topicmaps.impl.utils.TopicModificationManager;
import net.ontopia.utils.OntopiaUnsupportedException;
import net.ontopia.utils.SynchronizedCollectionFactory;

/**
 * INTERNAL: The in-memory transaction implementation.
 */

public class InMemoryTopicMapTransaction extends AbstractTopicMapTransaction {
  
  protected ObjectTreeManager otree;
  protected TopicModificationManager topicmods;
  TopicEvents te;

  protected InMemoryTopicMapTransaction(InMemoryTopicMapStore store) {
    this(store, null);
  }
  
  protected InMemoryTopicMapTransaction(InMemoryTopicMapStore store, InMemoryTopicMapTransaction parent) {

    // Activate transaction (note: must be activated at this point, because of dependencies)
    this.active = true;
    
    this.store = store;
    this.parent = parent;
    
    // Initialize collection factory
    this.cfactory = new SynchronizedCollectionFactory();

    // Create a new topic map using the factory
    this.topicmap = new TopicMap(this);
    EventManagerIF emanager = (EventManagerIF)topicmap;
    
    // Initialize topic map builder
    this.builder = new TopicMapBuilder((TopicMap)topicmap);
    
    // Register object tree event listener with store event manager
    this.otree = new ObjectTreeManager(emanager, cfactory);
    this.topicmods = new TopicModificationManager(emanager, cfactory);
    this.te = new TopicEvents(store);
    this.te.registerListeners(emanager);
    this.topicmods.addListener(this.te, "TopicIF.modified");
    
    // Register a subject identity cache object with the topic map
    SubjectIdentityCache sicache = new SubjectIdentityCache(this, cfactory);
    sicache.registerListeners(emanager, otree);
    ((TopicMap)topicmap).setSubjectIdentityCache(sicache);

    // Create new index manager
    this.imanager = new IndexManager(this, cfactory, emanager, otree);
  }

  public boolean validate() {
    return !invalid;
  }

  public TopicMapTransactionIF createNested() {
    // Nested transactions are not supported
    throw new OntopiaUnsupportedException("Nested transactions not supported.");
  }

  public ObjectTreeManager getObjectTreeManager() {
    return otree;
  }
  
}
