
package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.StringifierIF;

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs
 * by calling their getValue() method.
 */

public class NameStringifier implements StringifierIF {
  
  /**
   * INTERNAL: Stringifies the given name.
   * @param name object, cast to TopicNameIF or VariantNameIF
   * internally; the given name
   * @return string containing name value or "[No name]"
   */
  public String toString(Object name) {
    if (name == null)
      return "[No name]";
    if (name instanceof TopicNameIF)
      return ((TopicNameIF) name).getValue();
    else
      return ((VariantNameIF) name).getValue();
  }
  
}
