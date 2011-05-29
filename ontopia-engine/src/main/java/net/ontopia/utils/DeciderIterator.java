
package net.ontopia.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * INTERNAL: An iterator that uses a decider to filter the elements of
 * another iterator.</p>
 */

public class DeciderIterator<T> implements Iterator<T> {

  protected DeciderIF<T> decider;
  protected Iterator<T> iterator;

  protected boolean done;
  protected T next;
  
  public DeciderIterator(DeciderIF<T> decider, Iterator<T> iterator) {
    this.decider = decider;
    this.iterator = iterator;

    // Find the first applicable element.
    findNext();
  }

  protected void findNext() {
    // Loop over the remaining elements to find next applicable element.
    while (iterator.hasNext()) {
      T element = iterator.next();
      // Check to see if element is acceptable.
      if (decider.ok(element)) {
        next = element;
        return;
      }
    }
    // There are no more elements and we're done.
    done = true;
  }
  
  public boolean hasNext() {
    // If we're done there are no more elements in this iterator.
    if (done) return false;
    // We're not done and there are more elements.
    return true;
  }

  public T next() {
    // Throw exception if there are no more elements.
    if (done) throw new NoSuchElementException();
    // Locate next applicable element.
    T object = next;
    findNext();
    // Return element.
    return object;
  }

  public void remove() {
    // Delegate remove to underlying iterator.
    iterator.remove();
  }
  
}
