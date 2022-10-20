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

package net.ontopia.topicmaps.impl.tmapi2.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.tmapi2.LazySet;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;

/**
 * Implementation of the {@link TypeInstanceIndex} interface
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class TypeInstanceIndexImpl implements TypeInstanceIndex {

  private final TopicMapImpl topicMap;

  private final ClassInstanceIndexIF classInstanceIndex;

  public TypeInstanceIndexImpl(TopicMapImpl topicMap) {
    this.topicMap = topicMap;

    this.classInstanceIndex = (ClassInstanceIndexIF) topicMap.getWrapped()
        .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getAssociationTypes()
   */
  @Override
  public Collection<Topic> getAssociationTypes() {
    return new LazySet<Topic>(topicMap, classInstanceIndex
        .getAssociationTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.tmapi.index.TypeInstanceIndex#getAssociations(org.tmapi.core.Topic)
   */
  @Override
  public Collection<Association> getAssociations(Topic type) {
    return new LazySet<Association>(topicMap, classInstanceIndex
        .getAssociations(topicMap.unwrapTopic(type)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getNameTypes()
   */
  @Override
  public Collection<Topic> getNameTypes() {
    return new LazySet<Topic>(topicMap, classInstanceIndex.getTopicNameTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getNames(org.tmapi.core.Topic)
   */
  @Override
  public Collection<Name> getNames(Topic type) {
    return new LazySet<Name>(topicMap, classInstanceIndex
        .getTopicNames(topicMap.unwrapTopic(type)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getOccurrenceTypes()
   */
  @Override
  public Collection<Topic> getOccurrenceTypes() {
    return new LazySet<Topic>(topicMap, classInstanceIndex.getOccurrenceTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)
   */
  @Override
  public Collection<Occurrence> getOccurrences(Topic type) {
    return new LazySet<Occurrence>(topicMap, classInstanceIndex
        .getOccurrences(topicMap.unwrapTopic(type)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getRoleTypes()
   */
  @Override
  public Collection<Topic> getRoleTypes() {
    return new LazySet<Topic>(topicMap, classInstanceIndex
        .getAssociationRoleTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getRoles(org.tmapi.core.Topic)
   */
  @Override
  public Collection<Role> getRoles(Topic type) {
    return new LazySet<Role>(topicMap, classInstanceIndex
        .getAssociationRoles(topicMap.unwrapTopic(type)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getTopicTypes()
   */
  @Override
  public Collection<Topic> getTopicTypes() {
    return new LazySet<Topic>(topicMap, classInstanceIndex.getTopicTypes());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getTopics(org.tmapi.core.Topic)
   */
  @Override
  public Collection<Topic> getTopics(Topic type) {
    TopicIF ontType = null;
    if (type != null) {
      ontType = topicMap.unwrapTopic(type);
    }

    return new LazySet<Topic>(topicMap, classInstanceIndex.getTopics(ontType));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.TypeInstanceIndex#getTopics(org.tmapi.core.Topic[],
   * boolean)
   */
  @Override
  public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {

    Set<TopicIF> resultSet = new HashSet<TopicIF>();
    boolean first = true;
    for (Topic t : types) {
      if ((first) || (!matchAll)) {
        resultSet.addAll(classInstanceIndex.getTopics(topicMap.unwrapTopic(t)));
        first = false;
      } else {
        resultSet.retainAll(classInstanceIndex.getTopics(topicMap.unwrapTopic(t)));
      }
    }

    if (resultSet.isEmpty()) {
      return Collections.emptyList();
    }

    return new LazySet<Topic>(topicMap, resultSet);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#close()
   */
  @Override
  public void close() {
    // no-op
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isAutoUpdated()
   */
  @Override
  public boolean isAutoUpdated() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isOpen()
   */
  @Override
  public boolean isOpen() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#open()
   */
  @Override
  public void open() {
    // no-op
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#reindex()
   */
  @Override
  public void reindex() {
    // no-op
  }

}
