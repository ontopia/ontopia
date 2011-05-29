
package net.ontopia.topicmaps.nav2.plugins;

import java.util.Comparator;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Compares two objects which are classes that implement the
 * PluginIF interface. Use the title of the plugin.
 */
public class PluginComparator implements Comparator {

  /**
   * INTERNAL: default constructor.
   */
  public PluginComparator() {
  }

  /**
   * INTERNAL: Compares two PluginIF objects.
   */
  public int compare(Object o1, Object o2) {
    String value1, value2;

    try {
      value1 = ((PluginIF) o1).getTitle();
      value2 = ((PluginIF) o2).getTitle();

    } catch (ClassCastException e) {
      String msg = "PluginComparator Error: " +
        "This comparator only compares PluginIF objects.";
      throw new OntopiaRuntimeException(msg);
    }
    
    if (value1 == null) return 1;
    if (value2 == null) return -1;
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





