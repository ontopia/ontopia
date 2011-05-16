
// $Id: MergeReference.java,v 1.4 2008/05/28 10:30:49 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.StoreDeletedException;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.AbstractTopicMapReference;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A topic map reference that uses a TopicMapRepositoryIF to
 * retrieve a list of topic maps and create a new merged topic map
 * from them.
 *
 * @since 2.1
 */
public class MergeReference extends AbstractTopicMapReference {

  protected TopicMapStoreIF store;
  protected TopicMapStoreFactoryIF sfactory;
  protected TopicMapRepositoryIF repository;
  protected List refkeys;
  protected boolean reuse_store = true; // WARNING: always reusing store

  /**
   * INTERNAL: Creates a TopicMapReferenceIF which references the
   * result of merging a number of topic maps
   *
   * @param sfactory the store factory to use for creating the
   * resulting merged topic map.
   * @param repository the topic map repository to look up the topic maps that
   * are to be merged in.
   * @param refkeys a list of topic map reference key strings which
   * refers to the topic maps that are to be merged.
   * should be used.
   */ 
  public MergeReference(String id, String title, 
			TopicMapStoreFactoryIF sfactory, TopicMapRepositoryIF repository,
			List refkeys) {
    super(id, title);
    this.sfactory = sfactory;
    this.repository = repository;
    this.refkeys = refkeys;
  }

  public synchronized void open() {
    // ignore if already open
    if (isOpen()) return;
    if (isDeleted()) 
      throw new StoreDeletedException("Topic map has been deleted through this reference.");
    // make sure store is loaded
    if (reuse_store && store == null)
      store = createStore();

    this.isopen = true;
  }

  public synchronized TopicMapStoreIF createStore(boolean readonly) throws IOException {
    if (!isOpen()) open();

    if (reuse_store) {
      if (store != null) return store;
      store = createStore();
      return store;
    } else {
      return createStore();
    }
  }

  protected TopicMapStoreIF createStore() {
    try {
      // create new empty store
      TopicMapStoreIF store = sfactory.createStore();    
      TopicMapIF merged = store.getTopicMap();

      // merge in all referenced topic maps
      Iterator iter = refkeys.iterator();
      while (iter.hasNext()) {
        String refkey = (String)iter.next();
				TopicMapReferenceIF ref = repository.getReferenceByKey(refkey);
				TopicMapStoreIF s = ref.createStore(true);
        MergeUtils.mergeInto(merged, s.getTopicMap());
				s.close();
      }
      // register store
      store.setReference(this);
      return store;
      
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public synchronized void close() {    
    // close and dereference store
    if (store != null) {
      if (store.isOpen()) store.close();
      store = null;
    }
    this.isopen = false;
  }
  
}
