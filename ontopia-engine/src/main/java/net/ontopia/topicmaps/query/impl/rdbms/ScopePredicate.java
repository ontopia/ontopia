
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'scope(scoped, theme)' predicate.
 */
public class ScopePredicate
  extends net.ontopia.topicmaps.query.impl.basic.ScopePredicate
  implements JDOPredicateIF {

  public ScopePredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: scope(SCOPED, THEME)
    JDOValueIF jv_scoped = builder.createJDOValue(args[0]);
    JDOValueIF jv_theme = builder.createJDOValue(args[1]);
          
    // JDOQL: O.scope.contains(T)
    expressions.add(new JDOContains(new JDOField(jv_scoped, "scope"), jv_theme));
          
    // JDOQL: O.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_scoped, "topicmap"),
                                  new JDOObject(topicmap)));
          
    return true;
  }
  
}
