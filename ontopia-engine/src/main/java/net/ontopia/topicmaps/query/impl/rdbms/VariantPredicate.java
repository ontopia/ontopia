
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'variant' predicate.
 */
public class VariantPredicate
  extends net.ontopia.topicmaps.query.impl.basic.VariantPredicate
  implements JDOPredicateIF {

  public VariantPredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: variant(TOPICNAME, VARIANT)
    JDOValueIF jv_basename = builder.createJDOValue(args[0]);
    JDOValueIF jv_variant = builder.createJDOValue(args[1]);
    
    // JDOQL: V.basename = B
    expressions.add(new JDOEquals(new JDOField(jv_variant, "name"), jv_basename));
    //! expressions.add(new JDOContains(new JDOField(jv_basename, "variants"), jv_variant));

    expressions.add(new JDOEquals(new JDOField(jv_variant, "topicmap"),
                                  new JDOObject(topicmap)));
    
    return true;
  }
  
}
