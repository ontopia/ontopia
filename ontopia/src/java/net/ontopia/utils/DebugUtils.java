
// $Id: DebugUtils.java,v 1.3 2008/05/14 14:09:37 lars.garshol Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Useful debugging methods.
 */
public class DebugUtils {

  public static String toString(Object[] array) {
    if (array == null)
      return "null";
    
    return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(int[] array) {
    if (array == null)
      return "null";
    
    StringBuffer list = new StringBuffer("[");

    if (array.length > 0) {
      list.append(Integer.toString(array[0]));
    
      for (int ix = 1; ix < array.length; ix++) {
        list.append(", ");
        list.append(Integer.toString(array[ix]));
      }
    }
    
    list.append("]");
    return list.toString();
  }

  public static String toString(boolean[] array) {
    if (array == null)
      return "null";
    
    StringBuffer list = new StringBuffer("[");

    if (array.length > 0) {
      list.append(Boolean.toString(array[0]));
    
      for (int ix = 1; ix < array.length; ix++) {
        list.append(", ");
        list.append(Boolean.toString(array[ix]));
      }
    }
    
    list.append("]");
    return list.toString();
  }  
}
