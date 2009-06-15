// $Id: DefaultStringifier.java,v 1.6 2004/11/29 19:10:44 grove Exp $

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




