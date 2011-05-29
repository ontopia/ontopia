
package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'resource' predicate.
 */
public class ResourcePredicate
  extends net.ontopia.topicmaps.query.impl.basic.ResourcePredicate
  implements JDOPredicateIF {

  public ResourcePredicate(TopicMapIF topicmap) {
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

    JDOValueIF jv_object = builder.createJDOValue(args[0]);
    JDOValueIF jv_uri = builder.createJDOValue(args[1]);
          
    // JDOQL: O.value.address = U
    expressions.add(new JDOEquals(new JDOField(jv_object, "value"), jv_uri));
		expressions.add(new JDOEquals(new JDOField(jv_object, "datatype", "address"), 
																	builder.createJDOValue(DataTypes.TYPE_URI.getAddress())));

    //! // if variable: filter out nulls
    //! if (jv_uri.getType() == JDOValueIF.VARIABLE)
    //!   expressions.add(new JDONotEquals(jv_uri, new JDONull()));

    // JDOQL: O.topicmap = TOPICMAP
    expressions.add(new JDOEquals(new JDOField(jv_object, "topicmap"),
                                  new JDOObject(topicmap)));
          
    return true;
  }
  
}
