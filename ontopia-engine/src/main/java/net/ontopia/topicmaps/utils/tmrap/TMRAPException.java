
// $Id: TMRAPException.java,v 1.3 2006/05/08 07:18:31 grove Exp $

package net.ontopia.topicmaps.utils.tmrap;

import net.ontopia.utils.OntopiaException;

/**
 * INTERNAL: Thrown to show that there was an error at the TMRAP level.
 *
 * @since 3.1
 */
public class TMRAPException extends OntopiaException {
  public TMRAPException(String msg) {
    super(msg);
  }
  public TMRAPException(Throwable cause) {
    super(cause);
  }
  public TMRAPException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
