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

import java.util.Comparator;

/**
 * INTERNAL: Comparator that compares grabbed objects using a
 * comparator.
 */

@Deprecated
public class GrabberComparator<T, G> implements Comparator<T> {

  protected GrabberIF<T, G> grabber1;
  protected GrabberIF<T, G> grabber2;

  protected Comparator<G> comparator;
   
  public GrabberComparator(GrabberIF<T, G> grabber, Comparator<G> comparator) {
    this.grabber1 = grabber;
    this.comparator = comparator;
  }
 
  public GrabberComparator(GrabberIF<T, G> grabber1, GrabberIF<T, G> grabber2, Comparator<G> comparator) {
    this.grabber1 = grabber1;
    this.grabber2 = grabber2;
    this.comparator = comparator;
  }
 
  @Override
  public int compare(T object1, T object2) {
    // Grab objects
    G grabbed1 = grabber1.grab(object1);
    G grabbed2;
    if (grabber2 == null)
      grabbed2 = grabber1.grab(object2);
    else 
      grabbed2 = grabber2.grab(object2);

    // Compare grabbed objects
    return comparator.compare(grabbed1, grabbed2);    
  }
  
}




