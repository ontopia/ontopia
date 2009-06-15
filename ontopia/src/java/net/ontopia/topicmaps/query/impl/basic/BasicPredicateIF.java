
// $Id: BasicPredicateIF.java,v 1.4 2003/02/18 14:56:56 larsga Exp $

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.PredicateIF;

/**
 * INTERNAL: Represents a predicate in the basic implementation.
 */
public interface BasicPredicateIF extends PredicateIF {

  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException;
  
}
