
// $Id: InvalidQueryException.java,v 1.4 2005/12/14 13:08:34 grove Exp $

package net.ontopia.topicmaps.query.core;

/**
 * PUBLIC: This exception is used to indicate that there is something
 * wrong with the query, whether syntactically or semantically.
 */
public class InvalidQueryException extends QueryException 
{
  private static final long serialVersionUID = 2502041495982776532L;
  
  public InvalidQueryException(String msg) {
    super(msg);
  }
  public InvalidQueryException(Throwable cause) {
    super(cause);
  }
  public InvalidQueryException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
