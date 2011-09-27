
package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: A change log reader is a tuple reader that is used to
 * read a change log relation. For each tuple read it also returns the
 * type of change and an order value that says something about the
 * order in which the change occurred.
 */
public interface ChangelogReaderIF extends TupleReaderIF {

  /**
   * INTERNAL: Returns the type of change that the current tuple went
   * through.
   */
  public ChangeType getChangeType();

  /**
   * INTERNAL: Returns the current order value found in the
   * order-column. This information is used so that one can keep track
   * of incremental updates.
   */
  public String getOrderValue();
  
}
