
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the object-id predicate.
 */
public class ObjectIdPredicate
  extends net.ontopia.topicmaps.query.impl.basic.ObjectIdPredicate
  implements JDOPredicateIF {

  public ObjectIdPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }

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
