
package net.ontopia.topicmaps.core;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: An exception that is thrown when a transaction is
 * accessed without the transaction being active.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public class TransactionNotActiveException extends OntopiaRuntimeException {

  public TransactionNotActiveException(Throwable e) {
    super(e);
  }

  public TransactionNotActiveException(String message) {
    super(message);
  }

  public TransactionNotActiveException(String message, Throwable cause) {
    super(message, cause);
  }
  
}





