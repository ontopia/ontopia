
// $Id: TopicIndexIF.java,v 1.6 2008/07/17 10:14:07 lars.garshol Exp $

package net.ontopia.topicmaps.utils.tmrap;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
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
  public Collection getTopics(Collection indicators,
                              Collection sources,
                              Collection subjects);

  /*
   * Loads all the topics that are directly associated with the given topics.
   * @param two_steps If true, topics two steps out will also be loaded.
   */  
  public Collection loadRelatedTopics(Collection indicators,
                                      Collection sources,
                                      Collection subjects,
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
  public Collection getTopicPages(Collection indicators,
                                  Collection sources,
                                  Collection subjects);

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
  public TopicPages getTopicPages2(Collection indicators,
                                   Collection sources,
                                   Collection subjects);

  /**
   * Lets go of any underlying resources used by the index. Must be
   * called when used with the RDBMS backend.
   */
  public void close();
}
