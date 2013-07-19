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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: A utility class for producing full URIs from QNames. Allows
 * QName prefixes to be registered, and has a set of predefined QName
 * prefixes. Also allows topics to be looked up, via the QNameLookup
 * class.
 * @since 5.0.0
 */
public class QNameLookup {
  private QNameRegistry registry;
  private TopicMapIF topicmap;

  QNameLookup(QNameRegistry registry, TopicMapIF topicmap) {
    this.registry = registry;
    this.topicmap = topicmap;
  }

  public TopicIF lookup(String qname) {
    LocatorIF si = registry.resolve(qname);
    return topicmap.getTopicBySubjectIdentifier(si);
  }
}
