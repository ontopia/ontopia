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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: Grabber that grabs the most appropriate basename from a topic.
 */
public class TopicNameGrabber implements Function<TopicIF, TopicNameIF> {

  /**
   * PROTECTED: The comparator used to sort the base names.
   */
  protected Comparator<TopicNameIF> comparator;
 
  /**
   * INTERNAL: Creates a grabber; uses a BaseComparator with the given
   * scope.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new TopicNameComparator(scope);
  }
  
  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicNameGrabber(Comparator<TopicNameIF> comparator) {
    this.comparator = comparator;
  }
  
  /**
   * INTERNAL: Grabs the most appropriate base name for the given topic,
   * using the comparator established at creation to compare available
   * base names.
   *
   * @param topic A topic; formally an Object, but must implement TopicIF.
   *
   * @exception throws OntopiaRuntimeException if the given topic is not
   *            a TopicIF object.
   */
  @Override
  public TopicNameIF apply(TopicIF topic) {
    if (topic == null) {
      return null;
    }
    
    List<TopicNameIF> basenames = new ArrayList<TopicNameIF>(topic.getTopicNames());

    // If there is no base name return null
    if (basenames.isEmpty()) {
      return null;
    }

    // If there are multiple basenames rank them.    
    Collections.sort(basenames, comparator);
    return basenames.iterator().next();
  }

}
