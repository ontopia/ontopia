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

import java.util.Comparator;
import java.util.Collection;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

/**
 * INTERNAL: A Comparator for ordering TopicMapReferences lexically. It
 * compares the titles of references case-insensitively.
 */
public class TopicMapReferenceComparator implements Comparator {

  protected Collection scopes;

  /**
   * INTERNAL: Creates a reference comparator.
   */
  public TopicMapReferenceComparator() {
  }
  
  /**
   * INTERNAL: Compares two TopicMapReferenceIFs.
   */
  public int compare(Object o1, Object o2) {
    TopicMapReferenceIF tmr1, tmr2;
    String title1, title2;
    try {
      tmr1 = (TopicMapReferenceIF) o1;
      title1 = tmr1.getTitle();
      tmr2 = (TopicMapReferenceIF) o2;
      title2 = tmr2.getTitle();
    } catch (ClassCastException e) {
      String msg = "TopicMapReferenceComparator Error: " +
        "This comparator only compares TopicMapReferenceIFs";
      throw new OntopiaRuntimeException (msg);
    }
    return StringUtils.compareToIgnoreCase(title1, title2);
  }
  
}





