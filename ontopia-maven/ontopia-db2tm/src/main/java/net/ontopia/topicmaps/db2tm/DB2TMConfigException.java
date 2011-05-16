
// $Id: DB2TMConfigException.java,v 1.1 2006/06/28 08:21:49 grove Exp $

package net.ontopia.topicmaps.db2tm;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Thrown when configuration errors are detected by DB2TM.</p>
 */
public class DB2TMConfigException extends DB2TMException {

  public DB2TMConfigException(Throwable cause) {
    super(cause);
  }

  public DB2TMConfigException(String message) {
    super(message);
  }

  public DB2TMConfigException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
