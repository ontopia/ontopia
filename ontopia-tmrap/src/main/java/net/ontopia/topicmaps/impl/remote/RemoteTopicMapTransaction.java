
package net.ontopia.topicmaps.impl.remote;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.topicmaps.impl.basic.index.IndexManager;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapTransaction;
import net.ontopia.topicmaps.impl.basic.TopicMap;

/**
 * INTERNAL: The remote (remote in the sense that it deals with remote topics - 
 *           and not that the transactions work in a remote or distributed
 *           fashion) implementation of TopicMapTransaction.
 */
public class RemoteTopicMapTransaction extends InMemoryTopicMapTransaction {
    
  RemoteTopicMapTransaction(RemoteTopicMapStore store) {
    this(store, null);
  }
  
  RemoteTopicMapTransaction(RemoteTopicMapStore store, RemoteTopicMapTransaction parent) {
    super(store, parent);
    
    // Initialize remote topic map builder
    builder = new RemoteTopicMapBuilder((TopicMap)topicmap);
  }  
}
