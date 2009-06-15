
// $Id: DynamicFailurePredicate.java,v 1.3 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements dynamic association predicates.
 */
public class DynamicFailurePredicate
  extends net.ontopia.topicmaps.query.impl.basic.DynamicFailurePredicate
  implements JDOPredicateIF {

  public DynamicFailurePredicate(TopicIF type, LocatorIF base) {
    super(type, base);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    expressions.add(JDOBoolean.FALSE);
      
    return true;

  }

  
}
