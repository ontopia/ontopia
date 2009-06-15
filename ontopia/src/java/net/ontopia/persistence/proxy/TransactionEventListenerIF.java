// $Id: TransactionEventListenerIF.java,v 1.5 2005/07/12 09:37:39 grove Exp $

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
