
// $Id: ReifiesPredicate.java,v 1.14 2008/07/23 13:26:04 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Collection;
import java.util.List;

import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.query.jdo.JDOAnd;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONativeValue;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOPrimitive;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.persistence.query.jdo.JDOVariable;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.SourceLocator;
import net.ontopia.topicmaps.impl.rdbms.SubjectIndicatorLocator;
import net.ontopia.topicmaps.impl.rdbms.Topic;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.utils.ObjectUtils;

/**
 * INTERNAL: Implements the 'reifies(reifier, reified)' predicate.
 */
public class ReifiesPredicate
  extends net.ontopia.topicmaps.query.impl.basic.ReifiesPredicate
  implements JDOPredicateIF {

  public ReifiesPredicate(TopicMapIF topicmap) {
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
    
    // TOLOG: reifies(REIFIER, REIFIES)
    JDOValueIF jv_reifier = builder.createJDOValue(args[0]);

    if (args[0] instanceof TopicIF && args[1] instanceof TMObjectIF) {
            
      // Do direct predicate evaluation
			ReifiableIF reified = ((TopicIF)args[0]).getReified();
      if (ObjectUtils.equals(reified, args[1]))
        expressions.add(JDOBoolean.TRUE);
      else
        expressions.add(JDOBoolean.FALSE);

    } else {

      if (builder.isArgumentOfType(args[1], ReifiableIF.class)) {
				JDOValueIF jv_reified = builder.createJDOValue(args[1]);          
      
				expressions.add(new JDOEquals(new JDOField(jv_reified, "reifier"), jv_reifier));

				// JDOQL: REIFIER.topicmap = TOPICMAP
				expressions.add(new JDOEquals(new JDOField(jv_reifier, "topicmap"),
																			new JDOObject(topicmap)));
			} else {
				return false;
			}
    }
    
    return true;
  }
  
}
