/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * EXPERIMENTAL: An implementation that looks up topics in all
 * the given TopicIndexIFs and returns them.
 */
public class FederatedTopicIndex implements TopicIndexIF {
  protected List<TopicIndexIF> indexes;
  
  public FederatedTopicIndex(List<TopicIndexIF> indexes) {
    this.indexes = indexes;
  }
  
  @Override
  public Collection<TopicIF> getTopics(Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects) {
    Collection<TopicIF> topics = new ArrayList<TopicIF>();
    Iterator<TopicIndexIF> it = indexes.iterator();
    while (it.hasNext()) {
      TopicIndexIF index = it.next();
      topics.addAll(index.getTopics(indicators, sources, subjects));
    }
    return topics;
  }

  @Override
  public Collection<TopicIF> loadRelatedTopics(Collection<LocatorIF> indicators,
                                      Collection<LocatorIF> sources,
                                      Collection<LocatorIF> subjects,
                                      boolean two_step) {
    return getTopics(indicators, sources, subjects);
  }

  @Override
  public Collection<TopicPage> getTopicPages(Collection<LocatorIF> indicators,
                                  Collection<LocatorIF> sources,
                                  Collection<LocatorIF> subjects) {
    Collection<TopicPage> pages = new ArrayList<TopicPage>();
    Iterator<TopicIndexIF> it = indexes.iterator();
    while (it.hasNext()) {
      TopicIndexIF index = it.next();
      pages.addAll(index.getTopicPages(indicators, sources, subjects));
    }
    return pages;
  }

  @Override
  public TopicPages getTopicPages2(Collection<LocatorIF> indicators,
                                   Collection<LocatorIF> sources,
                                   Collection<LocatorIF> subjects) {
    TopicPages pages = new TopicPages();    
    Iterator<TopicIndexIF> it = indexes.iterator();
    while (it.hasNext()) {
      TopicIndexIF index = it.next();
      TopicPages currentPages = index.getTopicPages2(indicators, sources, subjects);
      pages.addAll(currentPages);
    }
    return pages;
  }

  @Override
  public void close() {
    Iterator<TopicIndexIF> it = indexes.iterator();
    while (it.hasNext()) {
      TopicIndexIF index = it.next();
      index.close();
    }
    indexes = null;
  }

}
