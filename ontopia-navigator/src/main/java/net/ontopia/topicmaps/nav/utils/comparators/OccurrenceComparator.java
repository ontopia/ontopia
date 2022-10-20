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
import java.util.Comparator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: A Comparator for ordering OccurrenceIFs alphabetically.
 */
public class OccurrenceComparator implements Comparator<OccurrenceIF> {

  protected Comparator<TopicIF> tc;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Occurrences using no
   * context.
   */
  public OccurrenceComparator() {
    tc = new TopicComparator();
  }

  /**
   * Constructor used to make a comparator which will compare
   * Occurrences using the context provided.
   */
  public OccurrenceComparator(Collection<TopicIF> baseNameContext) {
    tc = new TopicComparator(baseNameContext);
  }
  
  /**
   * Constructor used to make a comparator which will compare
   * Occurrences using the context provided.
   */
  public OccurrenceComparator(Collection<TopicIF> baseNameContext,
                              Collection<TopicIF> variantContext) {
    tc = new TopicComparator(baseNameContext, variantContext);
  }
  
  /**
   * Compares two OccurrenceIFs.
   */
  @Override
  public int compare(OccurrenceIF oc1, OccurrenceIF oc2) {
    int cmp;
    // first compare by type
    if (oc1.getType() == oc2.getType()) {
      cmp = 0;
    } else if (oc1.getType() == null) {
      cmp = 1;
    } else if (oc2.getType() == null) {
      cmp = -1;
    } else {
      cmp = tc.compare(oc1.getType(), oc2.getType());
    }

    // if that had no effect, try by locator
    if (cmp == 0) {
      if (oc1.getLocator() == oc2.getLocator()) {
        cmp = 0;
      } else if (oc1.getLocator() == null) {
        cmp = 1;
      } else if (oc2.getLocator() == null) {
        cmp = -1;
      } else {
        cmp = oc1.getLocator().getAddress().compareTo(oc2.getLocator().getAddress());
      }
    }

    // if that didn't work, try by value
    if (cmp == 0) {
      if (oc1.getValue().equals(oc2.getValue())) {
        cmp = 0;
      } else if (oc1.getValue() == null) {
        cmp = 1;
      } else if (oc2.getValue() == null) {
        cmp = -1;
      } else {
        cmp = oc1.getValue().compareTo(oc2.getValue());
      }
    } 
    
    return cmp;
  }
  
}





