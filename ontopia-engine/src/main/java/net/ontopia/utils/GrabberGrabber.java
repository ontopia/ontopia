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
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL: Grabber that makes the second grabber grab what the first
 * grabber grabs and so on. Any number of grabbers may be chained
 * together.</p>
 */

public class GrabberGrabber implements GrabberIF<Object, Object> {

  protected List<GrabberIF> grabbers = new ArrayList<GrabberIF>();
  
  public GrabberGrabber(GrabberIF... grabbers) {
    this.grabbers = new ArrayList<GrabberIF>(Arrays.asList(grabbers));
  }

  /**
   * Gets the chained grabbers.
   */  
  public List<GrabberIF> getGrabbers() {
    return grabbers;
  }

  /**
   * Sets the grabbers.
   */  
  public void setGrabbers(List<GrabberIF> grabbers) {
    this.grabbers = grabbers;
  }
  
  /**
   * Add grabber to the end of the grabber list.
   */  
  public void addGrabber(GrabberIF grabber) {
    grabbers.add(grabber);
  }
  
  public Object grab(Object object) {
    Object grabbed = object;
    // Loop over grabbers
    Iterator<GrabberIF> iter = grabbers.iterator();
    while (iter.hasNext()) {
      GrabberIF grabber = iter.next();
      grabbed = grabber.grab(grabbed);
    }
    return grabbed;
  }

}




