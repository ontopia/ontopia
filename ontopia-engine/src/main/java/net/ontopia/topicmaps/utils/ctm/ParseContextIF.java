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

package net.ontopia.topicmaps.utils.ctm;

import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

public interface ParseContextIF {

  void addPrefix(String prefix, LocatorIF locator);

  void addIncludeUri(LocatorIF uri);
  
  Set<LocatorIF> getIncludeUris();
  
  LocatorIF resolveQname(String qname);

  ValueGeneratorIF getTopicById(String id);
  
  ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid);

  ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc);

  ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid);

  ValueGeneratorIF getTopicByQname(String qname);

  TopicIF makeTopicById(String id);
  
  TopicIF makeTopicByItemIdentifier(LocatorIF itemid);

  TopicIF makeTopicBySubjectLocator(LocatorIF subjloc);

  TopicIF makeTopicBySubjectIdentifier(LocatorIF subjid);
  
  TopicIF makeAnonymousTopic();

  TopicIF makeAnonymousTopic(String wildcard_name);

  void registerTemplate(String name, Template template);

  Template getTemplate(String name, int paramcount);

  Map getTemplates();
}
