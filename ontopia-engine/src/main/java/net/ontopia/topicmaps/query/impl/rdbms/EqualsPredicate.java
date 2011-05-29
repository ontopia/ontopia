
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Implements the '=' predicate.
 */
public class EqualsPredicate
  extends net.ontopia.topicmaps.query.impl.basic.EqualsPredicate
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

    // Cannot execute if arguments are variables of type java.lang.Object
    if (args[0] instanceof Variable) {
      Class vt1 = builder.getVariableType(((Variable)args[0]).getName());
      if (Object.class.equals(vt1)) return false;
    }    
    if (args[1] instanceof Variable) {
      Class vt2 = builder.getVariableType(((Variable)args[1]).getName());
      if (Object.class.equals(vt2)) return false;
    }
      
    // TOLOG: $LEFT == $RIGHT
    JDOValueIF jv_left = builder.createJDOValue(args[0]); 
    JDOValueIF jv_right = builder.createJDOValue(args[1]); 

    // JDOQL: LEFT == RIGHT
    expressions.add(new JDOEquals(jv_left, jv_right));
    
    return true;
  }

  
}





