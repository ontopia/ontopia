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

package net.ontopia.topicmaps.core.index;

import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * PUBLIC: Index that holds information about the names of topics in
 * the topic map. The intention is to provide quick lookup of objects
 * by name.</p>
 */

public interface NameIndexIF extends IndexIF {
  
  /**
   * PUBLIC: Gets all topic names that have the given name value (in any scope).
   *
   * @param name_value A string; the value of a topic name.
   *
   * @return A collection of TopicNameIF objects with the given name value.
   */
  Collection<TopicNameIF> getTopicNames(String name_value);

  /**
   * PUBLIC: Gets all topic names that have the given name value (in any scope)
   * and topicNameType.
   *
   * @param value A string; the value of a topic name
   * @param topicNameType A TopicIF; the type of a topic name
   *
   * @return A collection of TopicNameIF objects with the given name value and type.
   * @since 5.4.0
   */
  public Collection<TopicNameIF> getTopicNames(String value, TopicIF topicNameType);

  /**
   * INTERNAL: Gets all variants that have the specified value 
   * independent of datatype.
   *
   * @return A collection of VariantNameIF objects.
   */
  Collection<VariantNameIF> getVariants(String value);

  /**
   * INTERNAL: Gets all variants that have the specified value and
   * datatype.
   *
   * @return A collection of VariantNameIF objects.
   * @since 4.0
   */
  Collection<VariantNameIF> getVariants(String value, LocatorIF datatype);
  
}





