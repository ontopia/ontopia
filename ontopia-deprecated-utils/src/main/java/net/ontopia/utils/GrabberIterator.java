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

import java.util.Iterator;

/**
 * INTERNAL: An iterator that uses a grabber to grab object from another
 * iterator.</p>
 */

@Deprecated
public class GrabberIterator<O, G> implements Iterator<G> {

  protected Iterator<O> iter;
  protected GrabberIF<O, G> grabber;
  
  public GrabberIterator(Iterator<O> iter, GrabberIF<O, G> grabber) {
    this.iter = iter;
    this.grabber = grabber;
  }
  
  @Override
  public boolean hasNext() {
    return iter.hasNext();
  }

  @Override
  public G next() {
    return grabber.grab(iter.next());
  }

  @Override
  public void remove() {
    iter.remove();
  }
  
}




