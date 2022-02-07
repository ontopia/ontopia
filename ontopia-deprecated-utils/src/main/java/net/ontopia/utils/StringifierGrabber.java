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

import java.util.Objects;

/**
 * INTERNAL: Grabber that grabs a stringified version of the object
 * given to it.
 */

@Deprecated
public class StringifierGrabber<T> implements GrabberIF<T, String> {

  protected StringifierIF<T> stringifier;

  public StringifierGrabber() {
    this(Objects::toString);
  }
  
  public StringifierGrabber(StringifierIF<T> stringifier) {
    setStringifier(stringifier);
  }

  /**
   * Gets the stringifier which is to be used.
   */
  public StringifierIF<T> getStringifier() {
    return stringifier;
  }
  
  /**
   * Sets the stringifier which is to be used.
   */
  public void setStringifier(StringifierIF<T> stringifier) {
    this.stringifier = stringifier;
  }
  
  @Override
  public String grab(T object) {
    return stringifier.toString(object);
  }
  
}




