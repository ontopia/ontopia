

// $Id: RemoteTopicMapBuilder.java,v 1.1 2008/01/11 12:22:20 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.remote;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.impl.basic.index.IndexManager;

/**
 * INTERNAL: The remote (remote in the sense that it deals with remote topics - 
 *           and not that the transactions work in a remote or distributed
 *           fashion) implementation of a topicMapBuilder.
 */
public class RemoteTopicMapBuilder extends TopicMapBuilder {
    
  RemoteTopicMapBuilder(TopicMap tm) {
    super(tm);
  }
  
  protected TopicIF createTopic() { // overrides method in parent
    TopicIF topic = new RemoteTopic(tm);
    tm.addTopic(topic);
    return topic;
  }

}
