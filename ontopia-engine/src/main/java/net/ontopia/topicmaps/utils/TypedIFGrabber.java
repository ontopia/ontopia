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

import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;

/**
 * INTERNAL: Grabber that grabs the type property of the TypedIF object
 * given to it.</p>
 */

public class TypedIFGrabber<T extends TypedIF> implements Function<T, TopicIF> {


  /**
   * INTERNAL: Grabs the topic which is the type of the given typedIF
   *
   * @param typed the given object; internally typecast to TypedIF
   * @return object which is the type; an object implementing TopicIF
   */  

  @Override
  public TopicIF apply(T typed) {
    return typed.getType();
  }

}





