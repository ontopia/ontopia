
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




