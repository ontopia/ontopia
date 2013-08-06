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

package net.ontopia.topicmaps.utils;

import java.util.Comparator;
import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: Comparator that compares object ids of TMObjectIF objects.
 */

public class ObjectIdComparator implements Comparator {

  public static final ObjectIdComparator INSTANCE = new ObjectIdComparator();

   /**
   * INTERNAL: compares the object ids of the given objects
   *
   * @param obj1 object; internally typecast to TMObjectIF
   * @param obj2 object; internally typecast to TMObjectIF
   * @return int; 0 if the two objects have the same object id; otherwise positive/negative
   *        according to compareTo on the (string) values of the object ids
   */ 

  public int compare(Object obj1, Object obj2) {
    return ((TMObjectIF)obj1).getObjectId().compareTo(((TMObjectIF)obj2).getObjectId());
  }
  
}





