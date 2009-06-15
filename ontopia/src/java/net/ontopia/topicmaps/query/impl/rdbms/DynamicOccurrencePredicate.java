
// $Id: DynamicOccurrencePredicate.java,v 1.12 2008/07/23 13:26:04 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.topicmaps.impl.rdbms.Occurrence;

/**
 * INTERNAL: Implements dynamic occurrence predicates.
 */
public class DynamicOccurrencePredicate
  extends net.ontopia.topicmaps.query.impl.basic.DynamicOccurrencePredicate
  implements JDOPredicateIF {

  public DynamicOccurrencePredicate(TopicMapIF topicmap, LocatorIF base, TopicIF type) {
    super(topicmap, base, type);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
    // variable as second argument is an unsupported variabel
    if (arguments.get(1) instanceof Variable)
      builder.addUnsupportedVariable((Variable)arguments.get(1));
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {

    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TOLOG: occtype(topic, value)
    JDOValueIF jv_occurs = builder.createJDOVariable("O", Occurrence.class);
      
    JDOValueIF jv_otype = builder.createJDOValue(type);
    JDOValueIF jv_topic = builder.createJDOValue(args[0]);
    JDOValueIF jv_value = builder.createJDOValue(args[1]);
      
    // JDOQL: O.type = OT
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "type"), jv_otype));
      
    // JDOQL: O.topic = T
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topic"), jv_topic));
      
    // JDOQL: (O.value = V)
    
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "value"), jv_value));
    
    // JDOQL: O.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_occurs, "topicmap"),
                                  new JDOObject(topicmap)));    
    return true;
  }

  
}
