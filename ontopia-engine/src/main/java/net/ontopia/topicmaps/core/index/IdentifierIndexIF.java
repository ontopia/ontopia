/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

/**
 * PUBLIC: Index that holds information about identifiers in the
 * topic map. The intention is to provide quick lookup of identifiers
 * by prefix and topicmap.
 * @since 5.4.0
 */
public interface IdentifierIndexIF extends IndexIF {
  
  /**
   * PUBLIC: Returns all the item identifiers of all the objects in the topicmap.
   * @return all the item identifiers of all the objects in the topicmap.
   * @since 5.4.0
   */
  Collection<LocatorIF> getItemIdentifiers();

  /**
   * PUBLIC: Returns all the item identifiers of all the objects in the topicmap that start with
   * the provided prefix.
   * @return all the matched item identifiers
   * @since 5.4.0
   */
  Collection<LocatorIF> getItemIdentifiersByPrefix(String prefix);
  
  /**
   * PUBLIC: Returns all the subject identifiers of all the topics in the topicmap.
   * @return all the subject identifiers of all the topics in the topicmap.
   * @since 5.4.0
   */
  Collection<LocatorIF> getSubjectIdentifiers();

  /**
   * PUBLIC: Returns all the subject identifiers of all the topics in the topicmap that start with
   * the provided prefix.
   * @return all the matched subject identifiers
   * @since 5.4.0
   */
  Collection<LocatorIF> getSubjectIdentifiersByPrefix(String prefix);
  
}
