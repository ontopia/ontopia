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

  public void addPrefix(String prefix, LocatorIF locator);

  public void addIncludeUri(LocatorIF uri);
  
  public Set<LocatorIF> getIncludeUris();
  
  public LocatorIF resolveQname(String qname);

  public ValueGeneratorIF getTopicById(String id);
  
  public ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid);

  public ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc);

  public ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid);

  public ValueGeneratorIF getTopicByQname(String qname);

  public TopicIF makeTopicById(String id);
  
  public TopicIF makeTopicByItemIdentifier(LocatorIF itemid);

  public TopicIF makeTopicBySubjectLocator(LocatorIF subjloc);

  public TopicIF makeTopicBySubjectIdentifier(LocatorIF subjid);
  
  public TopicIF makeAnonymousTopic();

  public TopicIF makeAnonymousTopic(String wildcard_name);

  public void registerTemplate(String name, Template template);

  public Template getTemplate(String name, int paramcount);

  public Map getTemplates();
}
