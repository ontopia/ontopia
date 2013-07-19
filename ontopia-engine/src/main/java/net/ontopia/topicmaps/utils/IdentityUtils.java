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

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;

/**
 * INTERNAL: Utilities for working with identities of topic map
 * objects.
 *
 * @since 3.4.4
 */
public class IdentityUtils {

  /**
   * INTERNAL: Looks up a topic map object by its symbolic id.
   */
  public static TMObjectIF getObjectBySymbolicId(TopicMapIF topicmap, String symbolicId) {
    LocatorIF loc = getSymbolicIdLocator(topicmap, symbolicId);
    if (loc != null)
      return topicmap.getObjectByItemIdentifier(loc);
    else
      return null;
  }

  /**
   * INTERNAL: Expands a symbolic id into a LocatorIF based on the
   * base locator of the given topic map..
   */
  public static LocatorIF getSymbolicIdLocator(TopicMapIF topicmap, String symbolicId) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    if (base != null)
      return base.resolveAbsolute('#' + symbolicId);
    else
      return null;
  }

  /**
   * INTERNAL: Returns the topic or topics with overlapping identities
   * in the given topic map. The topic argument should of course come
   * from a different topic map.
   *
   * @since 4.0
   */
  public static Collection<TopicIF> findSameTopic(TopicMapIF topicmap, TopicIF topic) {
    Set result = new HashSet();
    // item identifiers
    Iterator srclocs = topic.getItemIdentifiers().iterator();
    while (srclocs.hasNext()) {
      LocatorIF srcloc = (LocatorIF)srclocs.next();
      TMObjectIF o = topicmap.getObjectByItemIdentifier(srcloc);
      if (o instanceof TopicIF)
        result.add(o);
    }
    // subject identifiers
    Iterator subinds = topic.getSubjectIdentifiers().iterator();
    while (subinds.hasNext()) {
      LocatorIF subind = (LocatorIF)subinds.next();
      TopicIF t = topicmap.getTopicBySubjectIdentifier(subind);
      if (t != null)
        result.add(t);
    }
    // subject locators
    Iterator sublocs = topic.getSubjectLocators().iterator();
    while (sublocs.hasNext()) {
      LocatorIF subloc = (LocatorIF)sublocs.next();
      TopicIF t = topicmap.getTopicBySubjectLocator(subloc);
      if (t != null)
        result.add(t);
    }
    return result;
  }

}
