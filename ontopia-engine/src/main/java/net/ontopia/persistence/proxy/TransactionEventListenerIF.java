
package net.ontopia.persistence.proxy;

  
/**
 * INTERNAL: Interface for receiving notification when transaction
 * life-cycle events occur.<p>
 */

public interface TransactionEventListenerIF {

  /**
   * INTERNAL: Called when the transaction is stored.
   */  
  public void transactionStored(TransactionIF txn);
  
}
