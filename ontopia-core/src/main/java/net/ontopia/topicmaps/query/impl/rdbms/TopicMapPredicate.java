
// $Id: TopicMapPredicate.java,v 1.6 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'topicmap(topicmap)' predicate.
 */
public class TopicMapPredicate
  extends net.ontopia.topicmaps.query.impl.basic.TopicMapPredicate
  implements JDOPredicateIF {

  public TopicMapPredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: topicmap(TOPICMAP)
    JDOValueIF jv_topicmap = builder.createJDOValue(args[0]);
      
    // JDOQL: TM = TOPICMAP
    expressions.add(new JDOEquals(jv_topicmap,
                                  new JDOObject(topicmap)));
          
    return true;
  }
  
}
