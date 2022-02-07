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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL: Grabber that makes the second grabber grab what the first
 * grabber grabs and so on. Any number of grabbers may be chained
 * together.</p>
 */

@Deprecated
public class GrabberGrabber<O, G> implements GrabberIF<O, G> {

  protected GrabberIF<O, ?> firstGrabber;
  protected List<GrabberIF> additionalGrabbers = new ArrayList<GrabberIF>();
  
  public GrabberGrabber(GrabberIF<O, ?> firstGrabber, GrabberIF... additionalGrabbers) {
    this.firstGrabber = firstGrabber;
    this.additionalGrabbers = new ArrayList<GrabberIF>(Arrays.asList(additionalGrabbers));
  }

  /**
   * Gets the chained grabbers.
   */  
  public List<GrabberIF> getGrabbers() {
    List<GrabberIF> grabbers = new ArrayList(Collections.singleton(firstGrabber));
    grabbers.addAll(additionalGrabbers);
    return grabbers;
  }

  /**
   * Sets the grabbers.
   */  
  public void setGrabbers(List<GrabberIF> grabbers) {
    if (grabbers.size() < 1) { throw new OntopiaRuntimeException("Cannot set " + 
      "list of GrabberGrabber grabbers with less than one grabber."); }
    this.firstGrabber = (GrabberIF<O, ?>) grabbers.get(0);
    this.additionalGrabbers = grabbers.subList(1, grabbers.size());
}
  
  /**
   * Add grabber to the end of the grabber list.
   */  
  public void addGrabber(GrabberIF grabber) {
    additionalGrabbers.add(grabber);
  }
  
  @Override
  public G grab(O object) {
    // run firstGrabber
    Object grabbed = firstGrabber.grab(object);
    // Loop over grabbers
    Iterator<GrabberIF> iter = additionalGrabbers.iterator();
    while (iter.hasNext()) {
      GrabberIF grabber = iter.next();
      grabbed = grabber.grab(grabbed);
    }
    return (G) grabbed;
  }

}




