
// $Id: TransactionNotActiveException.java,v 1.2 2005/07/12 09:37:39 grove Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: Thrown when persistence related problems occur.</p>
 */

public class TransactionNotActiveException extends PersistenceRuntimeException {

  public TransactionNotActiveException() {
    super("Transaction is not active.");
  }

  public TransactionNotActiveException(Throwable e) {
    super("Transaction is not active.", e);
  }
  
}
