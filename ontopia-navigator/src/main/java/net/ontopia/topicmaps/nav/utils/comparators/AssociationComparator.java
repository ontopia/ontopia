/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * INTERNAL: A Comparator for ordering AssociationIFs alphabetically
 * after their type.
 */
public class AssociationComparator implements Comparator<AssociationIF> {

  protected Comparator<TopicIF> tc;
  protected Collection<TopicIF> scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Associations using no
   * context.
   */  
  public AssociationComparator() {
    this(null);
  }

  /**
   * Constructor used to make a comparator which will compare
   * Associations using the context provided.
   */
  public AssociationComparator(Collection<TopicIF> context) {
    this.scopes = context;
    if (scopes == null) {
      scopes = Collections.emptyList();
    }

    tc = Comparator.comparing(TopicStringifiers.getTopicNameStringifier(scopes).andThen(String::toUpperCase));
  }
  
  /**
   * Compares two AssociationIFs.
   */
  @Override
  public int compare(AssociationIF a1, AssociationIF a2) {
    return tc.compare(a1.getType(), a2.getType());
  }
  
}





