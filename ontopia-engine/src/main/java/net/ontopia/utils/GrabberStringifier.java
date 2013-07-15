
package net.ontopia.utils;

/**
 * INTERNAL: Stringifies the object that the grabber
 * grabs. DefaultStringifier will be used if no nested stringifier is
 * specified.</p>
 */

public class GrabberStringifier<T, G> implements StringifierIF<T> {

  protected GrabberIF<T, G> grabber;
  protected StringifierIF<? super G> stringifier;
  
  public GrabberStringifier(GrabberIF<T, G> grabber) {
    this(grabber, new DefaultStringifier<G>());
  }
  
  public GrabberStringifier(GrabberIF<T, G> grabber, StringifierIF<? super G> stringifier) {
    setGrabber(grabber);
    setStringifier(stringifier);
  }

  /**
   * Set the grabber which is to be used.
   */
  public void setGrabber(GrabberIF<T, G> grabber) {
    this.grabber = grabber;
  }

  /**
   * Set the stringifier which is to be used.
   */
  public void setStringifier(StringifierIF<? super G> stringifier) {
    this.stringifier = stringifier;
  }
  
  public String toString(T object) {
    return stringifier.toString(grabber.grab(object));
  }
  
}
