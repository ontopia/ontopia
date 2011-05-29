
package net.ontopia.topicmaps.nav2.impl.basic;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs
 * by calling their getValue() method. In addition it can be specified
 * which strings to use for the different fail-situations:
 * <ul>
 *  <li>the object is null (no base name/variant name existent)</li>
 *  <li>the value is null (null base name/variant name)</li>
 *  <li>the value is empty (empty string base name/variant name)</li>
 * </ul> 
 */
public class CustomNameStringifier implements StringifierIF {

  // define fallback values
  protected String stringNonExistent = "[No name]";
  protected String stringValueNull   = "[Null name]";
  protected String stringValueEmpty  = "[Empty name]";
  
  /**
   * INTERNAL: Stringifies the given name.
   *
   * @param name object, cast to TopicNameIF or VariantNameIF
   *             internally; the given name
   * @return string containing name value or "[No name]"
   */
  public String toString(Object name) {
    String stringName = null;
    if (name == null)
      return stringNonExistent;
    if (name instanceof TopicNameIF)
      stringName = ((TopicNameIF) name).getValue();
    else
      stringName = ((VariantNameIF) name).getValue();
    if (stringName == null)
      stringName = stringValueNull;
    else if (stringName.equals(""))
      stringName = stringValueEmpty;
    return stringName;
  }

  public void setStringNonExistent(String stringNonExistent) {
    this.stringNonExistent = stringNonExistent;
  }
  
  public String getStringNonExistent() {
    return stringNonExistent;
  }

  public void setStringValueNull(String stringValueNull) {
    this.stringValueNull = stringValueNull;
  }
  
  public String getStringValueNull() {
    return stringValueNull;
  }

  public void setStringValueEmpty(String stringValueEmpty) {
    this.stringValueEmpty = stringValueEmpty;
  }
  
  public String getStringValueEmpty() {
    return stringValueEmpty;
  }

}
