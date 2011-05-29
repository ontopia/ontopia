
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOFunction;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.impl.rdbms.TopicName;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'value-like' predicate.
 */
public class ValuePredicate
  extends net.ontopia.topicmaps.query.impl.basic.ValuePredicate
  implements JDOPredicateIF {

  public ValuePredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: value-like(OBJECT, MATCHING-VALUE)
    // TODO: Should check to see if second argument is of string type.
    JDOValueIF jv_object = builder.createJDOValue(args[0]);
    JDOValueIF jv_value = builder.createJDOValue(args[1]);

    String funcname = builder.getProperty("net.ontopia.topicmaps.query.impl.rdbms.ValuePredicate.function");

    if (funcname == null || builder.isArgumentOfType(args[0], TopicName.class)) {
      // JDOQL: B.value = V
      expressions.add(new JDOEquals(new JDOField(jv_object, "value"), jv_value));
    } else {
      // JDOQL: function(B.value) = V
      JDOFunction substr = new JDOFunction(funcname, String.class, jv_value);
      expressions.add(new JDOEquals(new JDOField(jv_object, "value"), substr));
      //! JDOFunction substr = new JDOFunction(funcname, String.class, new JDOField(jv_object, "value"));
      //! expressions.add(new JDOEquals(jv_value, substr));
    }
		if (!builder.isArgumentOfType(args[0], TopicName.class))
			expressions.add(new JDONotEquals(new JDOField(jv_object, "datatype", "address"), 
																			 builder.createJDOValue(DataTypes.TYPE_URI.getAddress())));

    // if variable: filter out nulls
    if (jv_value.getType() == JDOValueIF.VARIABLE)
      expressions.add(new JDONotEquals(jv_value, new JDONull()));

    // JDOQL: B.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_object, "topicmap"),
                                  new JDOObject(topicmap)));
          
    return true;
  }
  
}
