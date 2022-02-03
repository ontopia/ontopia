/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

@Deprecated
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
  @Override
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

  @Override
  public void clear() {
    getCollection().clear();
  }
  
  @Override
  public boolean contains(Object o) {
    return getCollection().contains(o);
  } 

  @Override
  public boolean containsAll(Collection<?> c) {
    return getCollection().containsAll(c);
  } 

  @Override
  public boolean equals(Object o) {
    return getCollection().equals(o);
  } 

  @Override
  public boolean isEmpty() {
    // If nested collection is empty this one is also.
    return getCollection().isEmpty();
  } 

  @Override
  public Iterator<G> iterator() {
    return getCollection().iterator();
  } 

  @Override
  public int size() {
    // The nested collection has the same size as this one.
    return getCollection().size();
  } 

  @Override
  public Object[] toArray() {
    return getCollection().toArray();    
  } 

  @Override
  public <G extends Object> G[] toArray(G[] a) {
    return getCollection().toArray(a);
  } 

  @Override
  public boolean add(G o) {
    return getCollection().add(o);
  }
  
  @Override
  public boolean addAll(Collection<? extends G> c) {
    return getCollection().addAll(c);
  } 

  @Override
  public boolean remove(Object o) {
    return getCollection().remove(o);
  } 

  @Override
  public boolean removeAll(Collection<?> c) {
    return getCollection().removeAll(c);
  } 

  @Override
  public boolean retainAll(Collection<?> c) {
    return getCollection().retainAll(c);
  } 

}




