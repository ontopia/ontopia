
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the coalesce predicate.
 */
public class CoalescePredicate
  extends net.ontopia.topicmaps.query.impl.basic.CoalescePredicate
  implements JDOPredicateIF {

  public void prescan(QueryBuilder builder, List arguments) {
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {

    // ISSUE: Cannot currently bind bind to object id via JDO
    return false;
  }

  
}
