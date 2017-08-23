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
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.utils.StringifierComparator;
import net.ontopia.utils.StringifierIF;

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
    return new StringifierComparator<TopicIF>(TopicStringifiers.getTopicNameStringifier(scope));
  }
  
  public static Comparator<TypedIF> getTypedIFComparator() {
    return tyc;
  }

  public static Comparator<TypedIF> getTypedIFComparator(Collection<TopicIF> scope) {
    return new TypedIFComparator(new StringifierComparator<TopicIF>(TopicStringifiers.getTopicNameStringifier(scope)));
  }

  public static <E> Comparator<E> getCaseInsensitiveComparator(StringifierIF<E> stringifier) {
    //! return new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(stringifier), new UpperCaseGrabber())));
    // NOTE: 1.3.4 - Replaced above with new and faster comparator:
    return new CaseInsensitiveStringifierComparator<E>(stringifier);
  }

  /**
   * INTERNAL: Case in-sensitive string comparator that is able to
   * handle null values.
   */ 
  public static class CaseInsensitiveStringifierComparator<E> implements Comparator<E> {
    protected StringifierIF<E> stringifier;
    public CaseInsensitiveStringifierComparator(StringifierIF<E> stringifier) {
      this.stringifier = stringifier;
    }
    public int compare(E obj1, E obj2) {
      String str1 = stringifier.toString(obj1);
      String str2 = stringifier.toString(obj2);      

      if (str1 == null)
        return (str2 == null ? 0 : 1);
      else
        if (str2 == null)
          return -1;
        else
          return str1.compareToIgnoreCase(str2);
    }
  }
}
