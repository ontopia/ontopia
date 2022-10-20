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

package net.ontopia.topicmaps.impl.basic.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;

/**
 * INTERNAL: Index providing lookups from base name value + scope to
 * the topic that has such base name characteristics. (TNC = Topic
 * Naming Constraint).
 */

public class TNCIndex implements IndexIF {

  protected NameIndexIF nameix;
  
  public TNCIndex(NameIndexIF nameix) {
    this.nameix = nameix;
  }

  public TNCIndex(TopicMapIF topicmap) {
    nameix = (NameIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
  }

  // --- TNCIndex methods

  /**
   * INTERNAL: Returns the topics that have a basename with the given
   * string value in the given scope.<p>
   *
   * This method is used to look up topics in the socalled topic name
   * space. Note that whether a single topic is returned depends on
   * whether the topic map have been completely processed or not.
   */
  public Collection<TopicIF> getTopics(String basename_string, Collection<TopicIF> scope) {
    HashSet<TopicIF> topics = new HashSet<TopicIF>();
    Iterator<TopicNameIF> it = nameix.getTopicNames(basename_string).iterator();
    while (it.hasNext()) {
      TopicNameIF bn = it.next();
      if (bn.getScope().equals(scope)) {
        topics.add(bn.getTopic());
      }
    }

    return topics;
  }

}
