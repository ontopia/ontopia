/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Map;
import java.util.List;
import java.util.Collection;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.impl.framework.MVSConfig;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;

/**
 * INTERNAL: Created when there are XML parse errors in the
 * configuration file so that we can report these errors in a proper
 * way.
 */
public class BrokenNavigatorConfiguration implements NavigatorConfigurationIF {
  private String errormsg;
  
  public BrokenNavigatorConfiguration(String errormsg) {
    this.errormsg = errormsg;
  }

  // ------------------------------------------------
  // Properties related methods
  // ------------------------------------------------
  
  public void addProperty(String name, String value) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public String getProperty(String name) {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public String getProperty(String name, String defaultValue) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public int getProperty(String name, int defaultValue) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Map getProperties() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  public void setProperties(Map properties) {
    throw new OntopiaRuntimeException(errormsg);
  }
  

  // ------------------------------------------------
  // Classmap related methods
  // ------------------------------------------------
  
  public void addClass(String shortcut, String fullClassName) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public String getClass(String shortcut) {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public Map getClassmap() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  public void setClassmap(Map classmap) {
    throw new OntopiaRuntimeException(errormsg);
  }


  // ------------------------------------------------
  // Properties related methods
  // ------------------------------------------------
  
  public void addAutoloadTopicMap(String topicmapId) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public boolean isAutoloadTopicMap(String topicmapId) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public boolean isAutoloadAllTopicMaps() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public Collection getAutoloadTopicMaps() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  public void setAutoloads(Collection autoloads) {
    throw new OntopiaRuntimeException(errormsg);
  }

  
  // ------------------------------------------------
  // MVS related methods
  // ------------------------------------------------

  @Override
  public MVSConfig getMVSConfig() {
    throw new OntopiaRuntimeException(errormsg);
  }

  public void setMVSConfig(MVSConfig mvsConfig) {
    throw new OntopiaRuntimeException(errormsg);
  }

  
  // --- model
  
  public void addModel(String name, String title, boolean isDefault) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Collection getModels() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public String getDefaultModel() {
    throw new OntopiaRuntimeException(errormsg);
  }

  // --- view
  
  public void addView(String name, String title, boolean isDefault) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Collection getViews() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public String getDefaultView() {
    throw new OntopiaRuntimeException(errormsg);
  }

  // --- skin
  
  public void addSkin(String name, String title, boolean isDefault) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Collection getSkins() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public String getDefaultSkin() {
    throw new OntopiaRuntimeException(errormsg);
  }

  
  // ------------------------------------------------
  // plugins related methods
  // ------------------------------------------------

  @Override
  public void addPlugin(PluginIF aPlugin) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public PluginIF getPlugin(String id) {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Collection getPlugins(String groupId) {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public Collection getPlugins() {
    throw new OntopiaRuntimeException(errormsg);
  }

  @Override
  public Collection getOrderedPlugins() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  @Override
  public List getPluginGroups() {
    throw new OntopiaRuntimeException(errormsg);
  }
  
  // ------------------------------------------------
  @Override
  public String toString() {
    return "[[[BrokenNavigatorConfiguration]]]";
  }
  
}
