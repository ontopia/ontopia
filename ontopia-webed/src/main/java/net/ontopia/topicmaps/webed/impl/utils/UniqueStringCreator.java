/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

/**
 * INTERNAL: Systematically generates strings that are guaranteed not to be
 * repeated.
 */
public class UniqueStringCreator {
    long basicCounter;
    UniqueStringCreator parent;
  
  public UniqueStringCreator() {
    basicCounter = 0;
    parent = null;
  }
  
  /**
   * Each time this method is called, it will return a String that was not
   * returned on any previous call, and thus will not be returned by any
   * subsequent call.
   */
  public String getNextUniqueString() {
    String retVal = getNextUniqueStringRecursively();
    increment();
    return retVal;
  }
  
  private String getNextUniqueStringRecursively() {
    String retVal = "";
    if (parent != null) {
      retVal += parent.getNextUniqueStringRecursively() + ".";
    }
    
    retVal += basicCounter;
    return retVal;
  }
  
  private void increment() {
    if (basicCounter == Long.MAX_VALUE) {
      basicCounter = 0;
      
      if (parent == null)
        parent = new UniqueStringCreator();
      else
        parent.increment();
    } else
      basicCounter++;
  }
  
  private static void evaluate() {
    UniqueStringCreator tested = new UniqueStringCreator();
    
    for (int j = 0; j < 6; j++) {
      for (int i = 0; i < 6; i++)
        System.out.println(tested.getNextUniqueString());
      System.out.println();
      tested.basicCounter = Long.MAX_VALUE - 1;
    }

    tested.parent.basicCounter = Long.MAX_VALUE;
    for (int i = 0; i < 6; i++)
      System.out.println(tested.getNextUniqueString());
    System.out.println();

    tested.basicCounter = Long.MAX_VALUE;
    for (int i = 0; i < 6; i++)
      System.out.println(tested.getNextUniqueString());
    System.out.println();
  }
}
