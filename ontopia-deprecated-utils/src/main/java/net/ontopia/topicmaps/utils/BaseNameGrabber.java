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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.GrabberIF;

/**
 * DEPRECATED: Grabber that grabs the most appropriate basename from a
 * topic.
 * @deprecated use TopicNameGrabber instead.
 */
@Deprecated
public class BaseNameGrabber implements GrabberIF<TopicIF, TopicNameIF> {

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
  public BaseNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new TopicNameComparator(scope);
  }
  
  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public BaseNameGrabber(Comparator<TopicNameIF> comparator) {
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
  public TopicNameIF grab(TopicIF _topic) {
    if (_topic == null)
      return null;
    
    Collection<TopicNameIF> basenames = _topic.getTopicNames();

    // If there is no base name return null
    if (basenames.isEmpty())
      return null;

    // If there are multiple basenames rank them.    
    TopicNameIF[] _basenames = basenames.toArray(new TopicNameIF[basenames.size()]);
    if (_basenames.length > 1)
      Arrays.sort(_basenames, comparator);
    return _basenames[0];
  }

}
