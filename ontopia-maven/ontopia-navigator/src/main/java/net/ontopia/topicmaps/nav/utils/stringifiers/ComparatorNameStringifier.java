// $Id: ComparatorNameStringifier.java,v 1.10 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Stringifier that stringifies TopicNameIFs and VariantNameIFs.
 */
public class ComparatorNameStringifier implements StringifierIF {
  
  /**
   * INTERNAL: Stringifies the given basename or variant name.
   *
   * @param name the name object to use; TopicNameIF or VariantNameIF
   * @return string containing name value or "~~~~~" if name not set
   */
  public String toString(Object name) {
    if (name == null)
      return "~~~~~";
    if (name instanceof TopicNameIF) {
      return ((TopicNameIF) name).getValue();
    } else {
      VariantNameIF vname = (VariantNameIF) name;
      if (vname.getValue() != null) 
        return vname.getValue();
      else
        return vname.getLocator().getAddress();      
    }
  }
  
}





