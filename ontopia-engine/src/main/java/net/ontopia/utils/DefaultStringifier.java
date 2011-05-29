
package net.ontopia.utils;

/**
 * INTERNAL: Stringifier that calls the toString method on the object.
 */

public class DefaultStringifier implements StringifierIF {

  public String toString(Object object) {
    if (object == null) return "null";
    return object.toString();
  }

}




