
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the '/=' predicate.
 */
public class NotEqualsPredicate
  extends net.ontopia.topicmaps.query.impl.basic.NotEqualsPredicate
  implements JDOPredicateIF {

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

    // TOLOG: $LEFT /= $RIGHT
    JDOValueIF jv_left = builder.createJDOValue(args[0]); 
    JDOValueIF jv_right = builder.createJDOValue(args[1]); 

    // JDOQL: LEFT != RIGHT
    expressions.add(new JDONotEquals(jv_left, jv_right));
    
    return true;
  }

  
}





