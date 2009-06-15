
// $Id: DirectInstanceOfPredicate.java,v 1.24 2006/04/27 16:03:12 grove Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'direct-instance-of' predicate.
 */
public class DirectInstanceOfPredicate
  extends net.ontopia.topicmaps.query.impl.basic.DirectInstanceOfPredicate
  implements JDOPredicateIF {

  public DirectInstanceOfPredicate(TopicMapIF topicmap) {
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

    // TOLOG: direct-instance-of ( INSTANCE, CLASS )
    if (args[0] instanceof TopicIF && args[1] instanceof TopicIF) {

      // Do direct predicate evaluation
      if (((TopicIF)args[0]).getTypes().contains(args[1]))
        expressions.add(JDOBoolean.TRUE);
      else
        expressions.add(JDOBoolean.FALSE);

    } else {                  
      JDOValueIF jv_instance = builder.createJDOValue(args[0]);
      JDOValueIF jv_class = builder.createJDOValue(args[1]);
        
      // JDOQL: INSTANCE.types.contains(CLASS)
      expressions.add(new JDOContains(new JDOField(jv_instance, "types"), jv_class));
        
      // JDOQL: CLASS.topicmap = TOPICMAP
      expressions.add(new JDOEquals(new JDOField(jv_instance, "topicmap"),
                                    new JDOObject(topicmap)));
    }
    
    return true;
  }

}





