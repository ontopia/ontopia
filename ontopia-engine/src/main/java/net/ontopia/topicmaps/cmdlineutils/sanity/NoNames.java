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
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * Finds all Topics with: 
 * - No names.
 * - No characteristics.
 * - No name in the unconstrained scope.
 * - No name in a given scope.
 */

public class NoNames {

  private TopicMapIF tm;
  private Collection nonametopics;

  public NoNames(TopicMapIF tm) {
    this.tm = tm;
    findNoNameTopics();
  }

  /**
   * Finds all the topics with no name in the Topic Map.
   */
  public void findNoNameTopics() {
    nonametopics = new ArrayList();
    Collection topics = tm.getTopics();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF t = (TopicIF)it.next();
      if (t.getTopicNames().isEmpty()) {
        nonametopics.add(t);
      }
    }
  }
  
  /**
   * Returns all topics and associations with no names.
   */
  public Collection getNoNameTopics() {
    return nonametopics;
  }
  

  /**
   * Returns all topics with no characteristics.
   */
  public Collection getNoCharacteristics() {
    Collection retur = new ArrayList();
    Collection topics = tm.getTopics();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();
      Collection candidate = topic.getTopicNames();
      if (candidate.size() == 0) {
        retur.add(topic);
      }
    }
    return retur;
  }


  /**
   * Returns all topics with no name in the unconstrained scope
   */
  public Collection getNoNameUnconstrained() {
    Collection retur = new ArrayList();
    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      boolean noname = true;
      Iterator it2 = topic.getTopicNames().iterator();
      while (noname && it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        noname = !bn.getScope().isEmpty();
      }

      if (noname) {
        retur.add(topic);
      }
    }
    return retur;
  }

  public String getTopicId(TopicIF topic) {
    String id = null;
    if (topic.getTopicMap().getStore().getBaseAddress() != null) {
      String base = topic.getTopicMap().getStore().getBaseAddress().getAddress();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF sloc = (LocatorIF) it.next();
        if (sloc.getAddress().startsWith(base)) {
          String addr = sloc.getAddress();
          id = addr.substring(addr.indexOf('#') + 1);
          break;
        }
      }
    }
    if (id == null) {
      id = "id" + topic.getObjectId();
    }
    return id;
  }    

}
