
package net.ontopia.topicmaps.db2tm;

import net.ontopia.utils.*;

/**
 * INTERNAL: A change log reader is a tuple reader that is used to
 * read a change log relation. For each tuple read it also returns the
 * type of change and an order value that says something about the
 * order in which the change occurred.
 */
public interface ChangelogReaderIF extends TupleReaderIF {

  // change type enumeration
  public static final int CHANGE_TYPE_UNKNOWN = 0;
  public static final int CHANGE_TYPE_CREATE = 1;
  public static final int CHANGE_TYPE_UPDATE = 2;
  public static final int CHANGE_TYPE_DELETE = 4;
  public static final int CHANGE_TYPE_IGNORE = 5;

  /**
   * INTERNAL: Returns the type of change that the current tuple went
   * through.
   */
  public int getChangeType();

  /**
   * INTERNAL: Returns the current order value found in the
   * order-column. This information is used so that one can keep track
   * of incremental updates.
   */
  public String getOrderValue();
  
}
