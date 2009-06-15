
// $Id: NamePredicate.java,v 1.3 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: The implementation of the 'name(topic, name-string)' predicate.
 */
public class NamePredicate
  extends net.ontopia.topicmaps.query.impl.basic.NamePredicate
  implements JDOPredicateIF {

  protected TopicMapIF topicmap;

  public NamePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {

    // TODO: This predicate is not yet supported natively. Delegate to
    // basic predicate.
    return false;
  }
  
}
