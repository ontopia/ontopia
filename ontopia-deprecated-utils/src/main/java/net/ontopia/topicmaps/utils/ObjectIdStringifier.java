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

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.utils.StringifierIF;

/**
 * INTERNAL: Stringifier that returns the object id of a topic map object.
 */

@Deprecated
public class ObjectIdStringifier implements StringifierIF<TMObjectIF> {
  
  /**
   * INTERNAL: Stringifies an arbitrary topicmap object, using its objectId
   *
   * @param tmobject TMObjectIF; the given topicmap object
   * @return string the topic map object id
   */
  @Override
  public String toString(TMObjectIF tmobject) {
    if (tmobject == null) return "null";
    return tmobject.getObjectId();
  }
  
}





