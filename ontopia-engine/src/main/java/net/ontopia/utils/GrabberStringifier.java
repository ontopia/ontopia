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
