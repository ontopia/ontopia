// $Id: CoreTestGenerator.java,v 1.7 2008/01/11 13:29:33 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic.test;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.test.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.impl.basic.*;

public class CoreTestGenerator extends AbstractCoreTestGenerator implements AbstractCoreTestGenerator.FactoryIF {

  public FactoryIF getFactory() {
    return this;
  }

  public TopicMapStoreIF makeStandaloneTopicMapStore() {
    return new InMemoryTopicMapStore();
  }

  public TopicMapReferenceIF makeTopicMapReference() {
    //! // Create new store
    //! InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    //! // Return topic map object
    //! return store.getTopicMap();

    return new StoreFactoryReference("basic", "Basic Implementation",
                                     new TopicMapStoreFactoryIF() {
                                       public TopicMapStoreIF createStore() {
                                         return new InMemoryTopicMapStore();
                                       }
                                     });
  }

  public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef) {
    topicmapRef.close();
    //! TopicMapStoreIF store = topicmap.getStore();
    //! store.close();
  }
  
}





