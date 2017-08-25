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

/**
 * EXPERIMENTAL: Common methods for all application contexts.
 */
public abstract class ApplicationContext implements ApplicationContextIF {
  private TopicMapView view; // null if no TM loaded
  private VizTopicMapConfigurationManager tmConfig;
  private VizPanel vpanel;
  
  /**
   * Get the view attached to this context
   *
   * @return Returns the view.
   */
  public TopicMapView getView() {
    return view;
  }
  
  /**
   * Set the view for this context
   *
   * @param view The view to set.
   */
  public void setView(TopicMapView view) {
    this.view = view;
  }
  
  /**
   * Get the Configuration Manager 
   *
   * @return Returns the tmConfig.
   */
  public VizTopicMapConfigurationManager getTmConfig() {
    return tmConfig;
  }
  
  /**
   * Set the Configuration Manager
   *
   * @param tmConfig The tmConfig to set.
   */
  public void setTmConfig(VizTopicMapConfigurationManager tmConfig) {
    this.tmConfig = tmConfig;
  }
  
  /**
   * Get the Panel 
   *
   * @return Returns the vpanel.
   */
  public VizPanel getVizPanel() {
    return vpanel;
  }
  
  /**
   * Set the Panel 
   *
   * @param vpanel The vpanel to set.
   */
  public void setVizPanel(VizPanel vpanel) {
    this.vpanel = vpanel;
  }
}
