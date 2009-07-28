
// $Id: QueryException.java,v 1.5 2005/12/14 13:08:34 grove Exp $

package net.ontopia.topicmaps.query.core;

import net.ontopia.utils.OntopiaException;

/**
 * PUBLIC: Common super-exception for all exceptions thrown by the
 * query engine.
 */
public class QueryException extends OntopiaException 
{
  private static final long serialVersionUID = 681171293109058887L;
  
  public QueryException(String msg) {
    super(msg);
  }
  public QueryException(Throwable cause) {
    super(cause);
  }
  public QueryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
