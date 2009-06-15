
// $Id: ChangelogReaderIF.java,v 1.5 2009/01/23 13:11:43 lars.garshol Exp $

package net.ontopia.topicmaps.db2tm;

import net.ontopia.utils.*;

/**
 * INTERNAL: A tuple reader is 
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
