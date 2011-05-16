
// $Id: OccurrencePredicate.java,v 1.10 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'occurrence($topic, $occurrence)' predicate.
 */
public class OccurrencePredicate
  extends net.ontopia.topicmaps.query.impl.basic.OccurrencePredicate
  implements JDOPredicateIF {

  public OccurrencePredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: occurrence(TOPIC, OCCURRENCE)
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
    JDOValueIF jv_occurs = builder.createJDOValue(args[1]);
      
    // JDOQL: O.topic = TOPIC
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topic"), jv_topic));
    //! expressions.add(new JDOContains(new JDOField(jv_topic, "occurs"), jv_occurs));

    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topicmap"),
                                  new JDOObject(topicmap)));
    
    return true;
  }
  
}
