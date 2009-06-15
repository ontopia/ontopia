
// $Id: DeciderUtils.java,v 1.3 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Utility methods for creating various kinds of useful
 * deciders.
 * @since 2.0
 */
public class DeciderUtils {

  /**
   * INTERNAL: Returns a decider which always returns true.
   */
  public static DeciderIF getTrueDecider() {
    return new StaticDecider(true);
  }

  /**
   * INTERNAL: Returns a decider which always returns false.
   */
  public static DeciderIF getFalseDecider() {
    return new StaticDecider(false);
  }
  
  // --- The actual decider classes

  static class StaticDecider implements DeciderIF {
    private boolean value;

    public StaticDecider(boolean value) {
      this.value = value;
    }
    
    public boolean ok(Object object) {
      return value;
    }
  }
  
}
