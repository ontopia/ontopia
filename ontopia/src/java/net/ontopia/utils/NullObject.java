
// $Id: NullObject.java,v 1.11 2007/09/27 07:00:43 geir.gronmo Exp $

package net.ontopia.utils;

/**
 * INTERNAL: A singleton null object for use where null cannot be used,
 * and an object is required.
 */

public final class NullObject {

  public static final NullObject INSTANCE = new NullObject();

  private NullObject() {
  }

  public boolean equals(Object other) {
    return (other instanceof NullObject);                                  
  }

  public int hashCode() {
    return 12345678;
  }
  
}




