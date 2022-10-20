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

package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent a : b pairs in tolog queries.
 */
public class Pair {
  protected Object first;
  protected Object second;
  
  public Pair(Object first, Object second) {
    this.first = first;
    this.second = second;
  }

  public Object getFirst() {
    return first;
  }

  public Object getSecond() {
    return second;
  }
  
  @Override
  public String toString() {
    return first + " : " + second;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair)) {
      return false;
    }

    Pair pair = (Pair)obj;
    return (first.equals(pair.first) &&
            second.equals(pair.second));
  }

  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }
  
}
