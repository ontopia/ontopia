/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Interface to define the application context.
 */
public interface ApplicationContextIF {
  /**
   * Returns the maximum number of nodes (locality) to display linked
   * to the central node.  Large numbers (> 3) can have a significant
   * performance impact when used, especially for large maps
   * 
   * @return the maximum allowed locality
   */
  int getMaxLocality();

  /**
   * Display the configuration frame for associations (edges)
   * 
   * @return The association configuration frame
   */
  TypesConfigFrame getAssocFrame();

  /**
   * Display the configuration frame for topics
   * 
   * @return the topic configuration frame
   */
  TypesConfigFrame getTopicFrame();

  /**
   * Get the default locality for the application.  This is usually 1
   * 
   * @return the default locality
   */
  int getDefaultLocality();

  /**
   * Is this an applet?
   * 
   * @return boolean indicating if this context is an applet
   */
  boolean isApplet();

  /**
   * Set the focus of the map to the specified topic
   * 
   * @param aTopic Topic to set as the central focus
   */
  void goToTopic(TopicIF aTopic);

  /**
   * Opens the supplied url string in a browser window. Which window
   * is used is defined by the 'propTarget' applet parameter.  Does
   * nothing for non applet context.
   * 
   * @param aUrl String representing the target url
   */
  void openPropertiesURL(String aUrl);

  /**
   * Assign the starting topic of the map to display.
   * 
   * @param aTopic Start Topic
   */
  void setStartTopic(TopicIF aTopic);

  /**
   * Returns the topic with the given subject identifier, if any.
   * 
   * @param locator the subject identifier
   * @param aTopicmap the topic map to look up in
   * 
   * @return topic with given subject identifier (or null if none)
   */
  TopicIF getTopicForLocator(LocatorIF locator, TopicMapIF aTopicmap);

  /**
   * Get a topic from the associated store
   * 
   * @param aTopic to read from the store
   */
  void loadTopic(TopicIF aTopic);

  /**
   * Set the node as the focus of the map
   * 
   * @param aNode node to focus on
   */
  void focusNode(TMAbstractNode aNode);

  /**
   * Set the scoping topic for the map
   * 
   * @param aScope topic to set scope
   */
  void setScopingTopic(TopicIF aScope);

  /**
   * Get the default scoping topic
   * 
   * @param aTopicmap map that contains the topic 
   * 
   * @return default scoping topic
   */
  TopicIF getDefaultScopingTopic(TopicMapIF aTopicmap);

  /**
   * Return the start topic defined on the map
   * 
   * @param aTopicmap map containing topic
   * 
   * @return start topic
   */
  TopicIF getStartTopic(TopicMapIF aTopicmap);

  /**
   * Return the menu of enabled items
   * 
   * @return menu containing enabled items
   */
  ParsedMenuFile getEnabledItemIds();

  /**
   * Store the panel containing the map
   * 
   * @param panel to save 
   */
  void setVizPanel(VizPanel panel);

  /**
   * Store the TopicMapConfiguration
   * 
   * @param config to save
   */
  void setTmConfig(VizTopicMapConfigurationManager config);

  /**
   * Set the view
   * 
   * @param view to set
   */
  void setView(TopicMapView view);
}
