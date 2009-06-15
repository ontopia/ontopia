package net.ontopia.topicmaps.impl.utils;

public interface TransactionEventListenerIF {

  public void transactionCommit(TopicMapTransactionIF transaction);

  public void transactionAbort(TopicMapTransactionIF transaction);

}
