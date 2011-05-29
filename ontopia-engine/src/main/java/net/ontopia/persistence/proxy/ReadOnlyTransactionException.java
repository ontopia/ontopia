
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
