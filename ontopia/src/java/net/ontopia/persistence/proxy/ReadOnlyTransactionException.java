
// $Id: ReadOnlyTransactionException.java,v 1.1 2006/02/06 09:15:16 grove Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: Thrown when modifications are attempted on a read-only
 * transaction.</p>
 */

public class ReadOnlyTransactionException extends PersistenceRuntimeException {

  public ReadOnlyTransactionException() {
    super("Cannot modify read-only transaction.");
  }

}
