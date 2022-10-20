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
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: A Comparator for ordering TopicNameIFs alphabetically
 * (case-independent).
 */
public class TopicNameComparator implements Comparator<TopicNameIF> {

  /**
   * Compares two TopicNameIFs.
   */
  @Override
  public int compare(TopicNameIF o1, TopicNameIF o2) {
    String value1 = o1.getValue();
    String value2 = o2.getValue();

    if (value1 == null) {
      return 1;
    }
    if (value2 == null) {
      return -1;
    }
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





