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

import java.awt.Color;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * EXPERIMENTAL: Interface to abstract the Vizigator front ends, and
 * allow for future front end implementations without changes to the
 * base code (i.e. eclipse plugin) (VizDesktop and Vizlet).
 */
public interface VizFrontEndIF {

  /**
   * Are the controls to this front end visible by default?
   *
   * @return true if the controls should be visible by default, false otherwise.
   */
  public boolean getDefaultControlsVisible();
  
  /**
   * Return true if this front end loads the topic map before 
   * generating any display, false if the display is generated
   * before the map is loaded.  In general, front ends that do not
   * allow for the map to be changed (servlets, etc) will pass
   * in a single map, while the desktop application allows you to
   * load and change the map. 
   *
   * @return true if the map is loaded before the display is
   * initialized, false otherwise
   */
  public boolean mapPreLoaded();

  /**
   * Does this front end use the general configuration.  Some front
   * ends (desktop) use the VizGeneralConfigurationManager, while
   * others exclusively use the VizTopicMapConfigurationManager.
   *
   * @return true if VizGeneralConfigurationManager is supported,
   * false otherwise
   */
  public boolean useGeneralConfig();

  /**
   * Returns the configuration frame that can be used to set colors for either Topics or Associations
   *
   * @param controller
   * @param isTopicConfig - is this a Topic or Association configuration frame
   * @return configuration frame - if True return TopicConfigurationFrame, else return AssociationConfigFrame
   */
   public TypesConfigFrame getTypesConfigFrame(VizController controller, boolean isTopicConfig);
  
  /**
   * Get the appropriate ApplicationContextIF for this front end.
   *
   * @return ApplicationContextIF for this front end
   */
  public ApplicationContextIF getContext();
  
  /**
   * Return the topic map that this front end is displaying
   *
   * @return current topic map
   */
  public TopicMapIF getTopicMap();
  
  /**
   * Set up the menus to control the filters
   *
   */
  public void configureFilterMenus();
  
  /**
   * set the color on a specific topic type
   *
   * @param type
   * @param c
   */
  public void setNewTypeColor(TopicIF type, Color c);
   
  /**
   * Get the url of the wallpaper for the background. 
   *
   * @return URL of the wallpaper file, null if no wallpaper
   */
  public String getWallpaper();

  /**
   * Get the URL of the config file
   *
   * @return URL of the config file, null if the default is to be used
   */
  public String getConfigURL();
}
