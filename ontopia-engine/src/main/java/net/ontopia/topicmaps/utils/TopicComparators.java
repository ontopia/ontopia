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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;

/**
 * INTERNAL: A collection of topic related comparators.
 */
public class TopicComparators {

  // Compare the stringified sortnames of the occurrence types
  protected static Comparator<TypedIF> tyc = getTypedIFComparator(Collections.<TopicIF>emptySet());

  private TopicComparators() {
    // don't call me
  }
  
  public static Comparator<TopicIF> getTopicNameComparator(Collection<TopicIF> scope) {
    return Comparator.comparing(TopicStringifiers.getTopicNameStringifier(scope));
  }
  
  public static Comparator<TypedIF> getTypedIFComparator() {
    return tyc;
  }

  public static Comparator<TypedIF> getTypedIFComparator(Collection<TopicIF> scope) {
    return new TypedIFComparator(Comparator.comparing(TopicStringifiers.getTopicNameStringifier(scope)));
  }

  public static <E> Comparator<E> getCaseInsensitiveComparator(Function<E, String> stringifier) {
    return Comparator.comparing(stringifier.andThen(String::toLowerCase));
  }
}
