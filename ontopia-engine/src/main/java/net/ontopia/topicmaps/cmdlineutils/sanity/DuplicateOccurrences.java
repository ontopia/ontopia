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

package net.ontopia.topicmaps.cmdlineutils.sanity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * Used to report all duplicate occurrences(same locator, same occurrence roletype)
 * on a single topic.
 */

public class DuplicateOccurrences {

  private TopicMapIF tm;

  public DuplicateOccurrences(TopicMapIF tm) {
    this.tm = tm;
  }

  /**
   * Returns a Collection of all the topics containing duplicate occurrences.
   */
  public Collection getDuplicateOccurrences() {
    Collection retur = new ArrayList();
    Collection topics = tm.getTopics();
    Iterator ittop = topics.iterator();
    while (ittop.hasNext()) {
      TopicIF t = (TopicIF)ittop.next();
      HashMap templocators = new HashMap();
      HashMap temproles    = new HashMap();
      
      Collection occurences = t.getOccurrences();
      Iterator itoccur = occurences.iterator();
      while (itoccur.hasNext()) {
        OccurrenceIF o = (OccurrenceIF)itoccur.next();
        LocatorIF l = o.getLocator();
        if (l != null) {
          if (temproles.containsKey(l.getAddress())) {
            retur.add(t);
          } else {
            temproles.put(l.getAddress(), null);
            templocators.put(o.getTopic(), null);
          }
        }
      }
    }
    return retur;
  }
}





