// $Id: GrabberCollection.java,v 1.9 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: A collection that uses a grabber to populate itself by
 * grabbing the individual objects of the nested collection. A decider
 * can be used to decide whether grabbed objects should be added to
 * the collection or not.</p>
 */

public class GrabberCollection<O, G> implements Collection<G>, CachedIF {

  protected Collection<O> coll;
  protected GrabberIF<O, G> grabber;
  protected DeciderIF<G> decider;

  protected boolean grabbed;
  protected Collection<G> grabbed_coll;
  
  public GrabberCollection(Collection<O> coll, GrabberIF<O, G> grabber) {
    this.coll = coll;
    this.grabber = grabber;
  }

  public GrabberCollection(Collection<O> coll, GrabberIF<O, G> grabber, DeciderIF<G> decider) {
    this(coll, grabber);
    this.decider = decider;
  }

  /**
   * Refreshes the collection by looping over the nested collection
   * and regrabbing its items. Any changes done to the previous
   * collection will be lost.
   */
  public void refresh() {
    grabbed_coll = new ArrayList<G>();
    Iterator<O> iter = coll.iterator();
    while (iter.hasNext()) {
      G grabbed = grabber.grab(iter.next());
      // Add grabbed to the grabbed collection if accepted by the decider
      if (decider == null || decider.ok(grabbed))
        grabbed_coll.add(grabbed);
    }
    grabbed = true;
  }
  
  protected Collection<G> getCollection() {
    if (grabbed) return grabbed_coll;
    refresh();
    return grabbed_coll;
  }

  public void clear() {
    getCollection().clear();
  }
  
  public boolean contains(Object o) {
    return getCollection().contains(o);
  } 

  public boolean containsAll(Collection<?> c) {
    return getCollection().containsAll(c);
  } 

  public boolean equals(Object o) {
    return getCollection().equals(o);
  } 

  public boolean isEmpty() {
    // If nested collection is empty this one is also.
    return getCollection().isEmpty();
  } 

  public Iterator<G> iterator() {
    return getCollection().iterator();
  } 

  public int size() {
    // The nested collection has the same size as this one.
    return getCollection().size();
  } 

  public Object[] toArray() {
    return getCollection().toArray();    
  } 

  public <G extends Object> G[] toArray(G[] a) {
    return getCollection().toArray(a);
  } 

  public boolean add(G o) {
    return getCollection().add(o);
  }
  
  public boolean addAll(Collection<? extends G> c) {
    return getCollection().addAll(c);
  } 

  public boolean remove(Object o) {
    return getCollection().remove(o);
  } 

  public boolean removeAll(Collection<?> c) {
    return getCollection().removeAll(c);
  } 

  public boolean retainAll(Collection<?> c) {
    return getCollection().retainAll(c);
  } 

}




