
// $Id: ContentStoreException.java,v 1.1 2003/10/01 14:07:25 grove Exp $

package net.ontopia.infoset.content;

import net.ontopia.utils.OntopiaException;

/**
 * INTERNAL: Thrown when problems occur in content store
 * implementations.</p>
 */

public class ContentStoreException extends OntopiaException {

  public ContentStoreException(Throwable cause) {
    super(cause);
  }

  public ContentStoreException(String message) {
    super(message);
  }

  public ContentStoreException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
