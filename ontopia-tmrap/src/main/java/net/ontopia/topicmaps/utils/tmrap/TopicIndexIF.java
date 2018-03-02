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

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * EXPERIMENTAL: An index through which information about topics with
 * a particular subject can be located, irrespective of where these
 * topics happen to be stored.
 */
public interface TopicIndexIF extends IndexIF {

  /**
   * Returns all topics the index knows about whose identity matches
   * one the of the locators passed as arguments.
   *
   * @param indicators A collection of subject identifiers as
   * LocatorIF objects.   
   * @param sources A collection of source locators as LocatorIF objects.
   * @param subjects A colleciton of subject locators as LocatorIF objects.
   *
   * @return Collection of TopicIF
   */
  Collection<TopicIF> getTopics(Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects);

  /*
   * Loads all the topics that are directly associated with the given topics.
   * @param two_steps If true, topics two steps out will also be loaded.
   */  
  Collection<TopicIF> loadRelatedTopics(Collection<LocatorIF> indicators,
                                      Collection<LocatorIF> sources,
                                      Collection<LocatorIF> subjects,
                                      boolean two_steps);

  /**
   * Returns all known topic pages for the topics whose identity
   * matches one of the locators passed as arguments.
   *
   * @param indicators A collection of subject identifiers as
   * LocatorIF objects.   
   * @param sources A collection of source locators as LocatorIF objects.
   * @param subjects A colleciton of subject locators as LocatorIF objects.
   *
   * @return Collection of TopicPage
   */
  Collection<TopicPage> getTopicPages(Collection<LocatorIF> indicators,
                                  Collection<LocatorIF> sources,
                                  Collection<LocatorIF> subjects);

  /**
   * Returns all known topic pages for the topics whose identity
   * matches one of the locators passed as arguments.
   *
   * @param indicators A collection of subject identifiers as
   * LocatorIF objects.   
   * @param sources A collection of source locators as LocatorIF objects.
   * @param subjects A colleciton of subject locators as LocatorIF objects.
   *
   * @return Collection of TopicPage
   */
  TopicPages getTopicPages2(Collection<LocatorIF> indicators,
                                   Collection<LocatorIF> sources,
                                   Collection<LocatorIF> subjects);

  /**
   * Lets go of any underlying resources used by the index. Must be
   * called when used with the RDBMS backend.
   */
  void close();
}
