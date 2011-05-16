
package net.ontopia.topicmaps.db2tm;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: Interface for extracting values from tuples. This
 * interfaces is, amongs other things, the basis for virtual column
 * implementations. The value is usually the result of processing the
 * values of the current tuple.
 */
public interface ValueIF {

  /**
   * INTERNAL: Returns the value of the virtual column.
   */
  public String getValue(String[] tuple);

}
