
// $Id: BaseLocatorPredicate.java,v 1.5 2008/01/11 13:29:34 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'base-locator(address)' predicate.
 */
public class BaseLocatorPredicate
  extends net.ontopia.topicmaps.query.impl.basic.BaseLocatorPredicate
  implements JDOPredicateIF {

  public BaseLocatorPredicate(TopicMapIF topicmap) {
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

    return false;

    //! // Interpret arguments
    //! Object[] args = arguments.toArray();
    //! 
    //! LocatorIF loc = topicmap.getStore().getBaseAddress();
    //! String baseloc = (loc == null ? null : loc.getAddress());
    //! 
    //! JDOValueIF jv_address = builder.createJDOValue(args[0]);
    //! JDOValueIF jv_baseloc = builder.createJDOValue(baseloc);
    //! 
    //! // JDOQL: A = tm.getStore().getBaseAddress()
    //! expressions.add(new JDOEquals(jv_address, jv_baseloc));
    //! 
    //! return true;
  }
  
}
