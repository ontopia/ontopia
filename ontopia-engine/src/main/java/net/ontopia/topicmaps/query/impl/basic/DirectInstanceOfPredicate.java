
package net.ontopia.topicmaps.query.impl.basic;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Implements the 'direct-instance-of' predicate using the indexes.
 */
public class DirectInstanceOfPredicate extends AbstractInstanceOfPredicate {
 
  public DirectInstanceOfPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }
  
  public String getName() {
    return "direct-instance-of";
  }

  // --- Data interface implementation

  protected void start() {
  }
  
  protected Collection getClasses(TopicIF instance) {
    return instance.getTypes();
  }

  protected Collection getInstances(TopicIF klass) {
    return index.getTopics(klass);
  }

  protected Collection getTypes() {
    return index.getTopicTypes();
  }

  protected Collection getSupertypes(TopicIF type) {
    return Collections.singleton(type);
  }  
}
