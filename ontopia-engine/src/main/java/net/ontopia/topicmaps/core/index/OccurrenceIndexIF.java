
package net.ontopia.topicmaps.core.index;

import java.util.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;

/**
 * INTERNAL: Index that holds information about occurrences in the
 * topic map. The intention is to provide quick lookup of occurrences
 * by value.
 *
 * @since 2.2
 */

public interface OccurrenceIndexIF extends IndexIF {

  /**
   * INTERNAL: Gets all occurrences that have the specified value
   * independent of datatype.
   *
   * @return A collection of OccurrenceIF objects.
   */
  public Collection<OccurrenceIF> getOccurrences(String value);

  /**
   * INTERNAL: Gets all occurrences that have the specified value and
   * datatype.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 4.0
   */
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype);

  /**
   * INTERNAL: Gets all occurrences of any datatype that have a value
   * starting with the specified prefix.
   *
   * @return A collection of OccurrenceIF objects.
   */
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix);

  /**
   * INTERNAL: Gets all occurrences that have the specifed datatype
   * and a value starting with the specified prefix.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 4.0
   */
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype);

  /**
   * INTERNAL: Gets all occurrence values that are greather than or
   * equal to the given value.
   */
  public Iterator<OccurrenceIF> getValuesGreaterThanOrEqual(String value);

  /**
   * INTERNAL: Gets all occurrence values that are smaller than or
   * equal to the given value.
   */
  public Iterator<OccurrenceIF> getValuesSmallerThanOrEqual(String value);
}
