
package net.ontopia.utils;

/**
 * INTERNAL: Decider that grabs an object and passes it to the
 * subdecider.
 */

public class GrabberDecider<T, G> implements DeciderIF<T> {

  protected GrabberIF<T, G> grabber;
  protected DeciderIF<G> decider;
   
  public GrabberDecider(GrabberIF<T, G> grabber, DeciderIF<G> decider) {
    this.grabber = grabber;
    this.decider = decider;
  }

  public boolean ok(T object) {
    return decider.ok(grabber.grab(object));
  }
  
}




