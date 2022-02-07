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
import net.ontopia.utils.StringifierIF;

/**
 * INTERNAL: Stringifier that generates a string representation of the
 * type property of the object given to it.</p>
 *
 * This stringifier uses a TypedIFGrabber internally to grab the
 * object's type property. Instances of this class can be configured
 * with a stringifier used to stringify the resulting topic.</p>
 */

@Deprecated
public class TypedIFStringifier<T extends TypedIF> implements StringifierIF<T> {

  protected Function<T, TopicIF> grabber = TypedIF::getType;
  protected StringifierIF<? super TopicIF> topic_stringifier;
  
  public TypedIFStringifier(StringifierIF<? super TopicIF> topic_stringifier) {
    this.topic_stringifier = topic_stringifier;
  }

  /**
   * Returns a string representation of the type property of the typed
   * object.
   *
   * @param typed An object implementing TypedIF.
   * @return The string that results from applying the configured
   * stringifier to the type extracted from the typed object.
   */
  @Override
  public String toString(T typed) {
    TopicIF type = grabber.apply(typed);
    return topic_stringifier.toString(type);
  }
  
}





