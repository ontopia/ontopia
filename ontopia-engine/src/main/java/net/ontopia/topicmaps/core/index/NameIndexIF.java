
package net.ontopia.topicmaps.core.index;

import java.util.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * PUBLIC: Index that holds information about the names of topics in
 * the topic map. The intention is to provide quick lookup of objects
 * by name.</p>
 */

public interface NameIndexIF extends IndexIF {
  
  /**
   * PUBLIC: Gets all topic names that have the given name value (in any scope).
   *
   * @param name_value A string; the value of a topic name.
   *
   * @return A collection of TopicNameIF objects with the given name value.
   */
  public Collection<TopicNameIF> getTopicNames(String name_value);

  /**
   * INTERNAL: Gets all variants that have the specified value 
   * independent of datatype.
   *
   * @return A collection of VariantNameIF objects.
   */
  public Collection<VariantNameIF> getVariants(String value);

  /**
   * INTERNAL: Gets all variants that have the specified value and
   * datatype.
   *
   * @return A collection of VariantNameIF objects.
   * @since 4.0
   */
  public Collection<VariantNameIF> getVariants(String value, LocatorIF datatype);
  
}





