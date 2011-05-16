
// $Id: TopicNamePredicate.java,v 1.12 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'topic-name' predicate.
 */
public class TopicNamePredicate
  extends net.ontopia.topicmaps.query.impl.basic.TopicNamePredicate
  implements JDOPredicateIF {

  public TopicNamePredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: topic-name(TOPIC, NAME)
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
    JDOValueIF jv_name = builder.createJDOValue(args[1]);
    
    // JDOQL: N.topic = T
    expressions.add(new JDOEquals(new JDOField(jv_name, "topic"), jv_topic));
    //! expressions.add(new JDOContains(new JDOField(jv_topic, "basenames"), jv_name));

    expressions.add(new JDOEquals(new JDOField(jv_name, "topicmap"),
                                  new JDOObject(topicmap)));
    
    return true;
  }
  
}
