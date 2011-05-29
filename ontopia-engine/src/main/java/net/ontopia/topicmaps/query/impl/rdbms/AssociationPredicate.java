
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'association($association)' predicate.
 */
public class AssociationPredicate
  extends net.ontopia.topicmaps.query.impl.basic.AssociationPredicate
  implements JDOPredicateIF {

  public AssociationPredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: association(ASSOCIATION)
    JDOValueIF jv_assoc = builder.createJDOValue(args[0]);
    
    // JDOQL: A.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_assoc, "topicmap"),
                                    new JDOObject(topicmap)));
    
    return true;
  }
  
}
