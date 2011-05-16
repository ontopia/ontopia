
package net.ontopia.utils;

/**
 * INTERNAL: Useful debugging methods.
 */
public class DebugUtils {

  public static String decodeString(String str) {
    StringBuilder buf = new StringBuilder(str.length() * 5);
    buf.append(str + " [");

    for (int ix = 0; ix < str.length(); ix++) {
      if (ix > 0)
        buf.append(", ");
      buf.append((int) str.charAt(ix));
    }
    
    buf.append("]");

    return buf.toString();
  }

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
