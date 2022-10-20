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
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Index that holds information about occurrences in the
 * topic map. The intention is to provide quick lookup of occurrences
 * by value.
 *
 * @since 2.2
 */

public interface OccurrenceIndexIF extends IndexIF {

  /**
   * INTERNAL: Gets all occurrences that have the specified value
   * independent of datatype.
   *
   * @return A collection of OccurrenceIF objects.
   */
  Collection<OccurrenceIF> getOccurrences(String value);

  /**
   * INTERNAL: Gets all occurrences that have the specified value
   * and occurrenceType independent of datatype.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 5.4.0
   */
  public Collection<OccurrenceIF> getOccurrences(String value, TopicIF occurrenceType);

  /**
   * INTERNAL: Gets all occurrences that have the specified value and
   * datatype.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 4.0
   */
  Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype);

  /**
   * INTERNAL: Gets all occurrences that have the specified value,
   * datatype and occurrenceType.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 5.4.0
   */
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype, TopicIF occurrenceType);

  /**
   * INTERNAL: Gets all occurrences of any datatype that have a value
   * starting with the specified prefix.
   *
   * @return A collection of OccurrenceIF objects.
   */
  Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix);

  /**
   * INTERNAL: Gets all occurrences that have the specifed datatype
   * and a value starting with the specified prefix.
   *
   * @return A collection of OccurrenceIF objects.
   * @since 4.0
   */
  Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype);

  /**
   * INTERNAL: Gets all occurrence values that are greather than or
   * equal to the given value.
   */
  Iterator<String> getValuesGreaterThanOrEqual(String value);

  /**
   * INTERNAL: Gets all occurrence values that are smaller than or
   * equal to the given value.
   */
  Iterator<String> getValuesSmallerThanOrEqual(String value);
}
