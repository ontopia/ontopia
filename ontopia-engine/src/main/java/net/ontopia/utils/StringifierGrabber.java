
package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Grabber that grabs a stringified version of the object
 * given to it.
 */

public class StringifierGrabber implements GrabberIF {

  protected StringifierIF stringifier;

  public StringifierGrabber() {
    this(new DefaultStringifier());
  }
  
  public StringifierGrabber(StringifierIF stringifier) {
    setStringifier(stringifier);
  }

  /**
   * Gets the stringifier which is to be used.
   */
  public StringifierIF getStringifier() {
    return stringifier;
  }
  
  /**
   * Sets the stringifier which is to be used.
   */
  public void setStringifier(StringifierIF stringifier) {
    this.stringifier = stringifier;
  }
  
  public Object grab(Object object) {
    return stringifier.toString(object);
  }
  
}




