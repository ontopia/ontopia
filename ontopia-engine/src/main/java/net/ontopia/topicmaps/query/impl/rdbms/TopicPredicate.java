
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'topic(topic)' predicate.
 */
public class TopicPredicate
  extends net.ontopia.topicmaps.query.impl.basic.TopicPredicate
  implements JDOPredicateIF {

  public TopicPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TOLOG: topic(TOPIC)
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
      
    // JDOQL: T.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_topic, "topicmap"),
                                  new JDOObject(topicmap)));
          
    return true;
  }
  
}
