
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'type(typed, type)' predicate.
 */
public class TypePredicate
  extends net.ontopia.topicmaps.query.impl.basic.TypePredicate
  implements JDOPredicateIF {

  public TypePredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: type(TYPED, TYPE)
    if (args[0] instanceof TypedIF && args[1] instanceof TopicIF) {

      // Do direct predicate evaluation
      if (args[1].equals(((TypedIF)args[0]).getType()))
        expressions.add(JDOBoolean.TRUE);
      else
        expressions.add(JDOBoolean.FALSE);
            
    } else {                  
            
      JDOValueIF jv_typed = builder.createJDOValue(args[0]);
      JDOValueIF jv_type = builder.createJDOValue(args[1]);
            
      // JDOQL: O.type = T
      expressions.add(new JDOEquals(new JDOField(jv_typed, "type"), jv_type));

      // if variable: filter out nulls
      if (jv_type.getType() == JDOValueIF.VARIABLE)
        expressions.add(new JDONotEquals(jv_type, new JDONull()));
            
      // JDOQL: O.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_typed, "topicmap"),
                                    new JDOObject(topicmap)));
    }
          
    return true;
  }
  
}
