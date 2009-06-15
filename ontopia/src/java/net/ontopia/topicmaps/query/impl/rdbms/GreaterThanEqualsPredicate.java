
// $Id: GreaterThanEqualsPredicate.java,v 1.5 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOFunction;
import net.ontopia.persistence.query.jdo.JDOValueExpression;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'gteq' predicate.
 */
public class GreaterThanEqualsPredicate
  extends net.ontopia.topicmaps.query.impl.basic.GreaterThanEqualsPredicate
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

    // TOLOG: $LEFT gteq $RIGHT
    JDOValueIF jv_left = builder.createJDOValue(args[0]); 
    JDOValueIF jv_right = builder.createJDOValue(args[1]); 

    // JDOQL: LEFT >= RIGHT
    expressions.add(new JDOValueExpression(new JDOFunction(">=", Boolean.class, jv_left, jv_right)));
    
    return true;
  }

  
}





