// $Id: Stringified.java,v 1.6 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

/**
 * INTERNAL: Utility class that wraps an object and a stringifier for
 * use with the Object.toString() method. This class is useful when
 * code calls the default toString() method on objects.</p>
 *
 * Ideally is should be possible to call the
 * StringifierIF.toString(Object) methods directly, but sometimes this
 * is impossible especially when working with code that you yourself
 * doesn't control. Thus you can wrap your object and stringifier in
 * an object of this class to gain the power of stringifiers.</p>
 *
 * The Swing components generally use the toString methods on the
 * objects for display. This means that you're not able to use
 * stringifiers with these objects. It is where this class comes to he
 * rescue.</p>
 */

public class Stringified {

  protected Object object;
  protected StringifierIF stringifier;

  public Stringified(Object object, StringifierIF stringifier) {
    this.object = object;
    this.stringifier = stringifier;
  }

  public Object getObject() {
    return object;
  }
  
  public String toString() {
    return stringifier.toString(object);
  }
}




