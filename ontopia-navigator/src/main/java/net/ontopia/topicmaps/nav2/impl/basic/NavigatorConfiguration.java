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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.impl.framework.MVSConfig;
import net.ontopia.topicmaps.nav2.plugins.PluginComparator;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.plugins.PluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A configuration holder class for storing
 * and providing information about the application
 * configuration.
 */
public class NavigatorConfiguration implements NavigatorConfigurationIF {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(NavigatorConfiguration.class.getName());

  // constants
  public final static String AUTOLOAD_ALL_KEY = "~all";
  
  // members
  protected Map properties;
  protected Map classmap;
  protected Map plugins;
  protected MVSConfig mvsConfig;

  protected Set autoloads;
  protected boolean isAutoloadAll;

  
  /**
   * INTERNAL: default constructor.
   */
  public NavigatorConfiguration() {
    properties = new HashMap();
    classmap = new HashMap();
    plugins = new HashMap();
    mvsConfig = new MVSConfig();
    
    autoloads = new HashSet();
    isAutoloadAll = false;
  }

  // ------------------------------------------------
  // Properties related methods
  // ------------------------------------------------
  
  public void addProperty(String name, String value) {
    properties.put(name, value);
  }

  @Override
  public String getProperty(String name) {
    return getProperty(name, "");
  }
  
  @Override
  public String getProperty(String name, String defaultValue) {
    String value = (String)properties.get(name);
    if (value == null) {
      return defaultValue;
    } else {
      return value;
    }
  }

  @Override
  public int getProperty(String name, int defaultValue) {
    try {
      String value = (String)properties.get(name);
      if (value == null) {
        return defaultValue;
      } else {
        return Integer.parseInt(value);
      }
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  @Override
  public Map getProperties() {
    return this.properties;
  }
  
  public void setProperties(Map properties) {
    this.properties = properties;
  }
  

  // ------------------------------------------------
  // Classmap related methods
  // ------------------------------------------------
  
  public void addClass(String shortcut, String fullClassName) {
    classmap.put(shortcut, fullClassName);
  }

  @Override
  public String getClass(String shortcut) {
    if (classmap.get(shortcut) != null) {
      return (String) classmap.get(shortcut);
    } else {
      return "";
    }
  }
  
  @Override
  public Map getClassmap() {
    return this.classmap;
  }
  
  public void setClassmap(Map classmap) {
    this.classmap = classmap;
  }


  // ------------------------------------------------
  // Properties related methods
  // ------------------------------------------------
  
  public void addAutoloadTopicMap(String topicmapId) {
    if (topicmapId.equals(AUTOLOAD_ALL_KEY)) {
      isAutoloadAll = true;
    } else {
      autoloads.add(topicmapId);
    }
  }

  @Override
  public boolean isAutoloadTopicMap(String topicmapId) {
    return autoloads.contains(topicmapId);
  }

  @Override
  public boolean isAutoloadAllTopicMaps() {
    return isAutoloadAll;
  }
  
  @Override
  public Collection getAutoloadTopicMaps() {
    return this.autoloads;
  }
  
  public void setAutoloads(Collection autoloads) {
    this.autoloads = new HashSet(autoloads);
  }

  
  // ------------------------------------------------
  // MVS related methods
  // ------------------------------------------------

  @Override
  public MVSConfig getMVSConfig() {
    return mvsConfig;
  }

  public void setMVSConfig(MVSConfig mvsConfig) {
    this.mvsConfig = mvsConfig;
  }

  
  // --- model
  
  public void addModel(String name, String title, boolean isDefault) {
    mvsConfig.addModel(name, title);
    if (isDefault) {
      mvsConfig.setModel(name);
    }
  }

  @Override
  public Collection getModels() {
    return mvsConfig.getModels();
  }
  
  @Override
  public String getDefaultModel() {
    return mvsConfig.getModel();
  }

  // --- view
  
  public void addView(String name, String title, boolean isDefault) {
    mvsConfig.addView(name, title);
    if (isDefault) {
      mvsConfig.setView(name);
    }
  }

  @Override
  public Collection getViews() {
    return mvsConfig.getViews();
  }
  
  @Override
  public String getDefaultView() {
    return mvsConfig.getView();
  }

  // --- skin
  
  public void addSkin(String name, String title, boolean isDefault) {
    mvsConfig.addSkin(name, title);
    if (isDefault) {
      mvsConfig.setSkin(name);
    }
  }

  @Override
  public Collection getSkins() {
    return mvsConfig.getSkins();
  }
  
  @Override
  public String getDefaultSkin() {
    return mvsConfig.getSkin();
  }

  
  // ------------------------------------------------
  // plugins related methods
  // ------------------------------------------------

  @Override
  public void addPlugin(PluginIF aPlugin) {
    plugins.put(aPlugin.getId(), aPlugin);
  }

  @Override
  public PluginIF getPlugin(String id) {
    return (PluginIF) plugins.get(id);
  }

  @Override
  public Collection getPlugins(String groupId) {
    if (groupId == null) {
      groupId = "";
    }

    // generate Property name to look up
    StringBuilder propName = new StringBuilder(PLUGINS_ORDER);
    if (!groupId.isEmpty()) {
      propName.append("_" + groupId);
    }
    
    // get string which specifies sort order of plugins
    String orderProp = getProperty(propName.toString());
    if ((orderProp == null || orderProp.isEmpty()) && groupId.isEmpty()) {
      return plugins.values();
    }

    List orderedPlugins = new ArrayList();

    // -- first put in plugins which are specified by sort order
    if (!orderProp.isEmpty()) {
      String[] order = StringUtils.split(orderProp);
      for (int ix = 0; ix < order.length; ix++) {
        PluginIF plugin = (PluginIF) plugins.get(order[ix]);
        if (plugin != null) {
          if (groupId == null ||
              (groupId != null && PluginUtils.inPluginGroups(groupId,
                                                             plugin.getGroups()))) {
            orderedPlugins.add(plugin);
          }
        } else {
          log.warn("property " + propName +
                 " contains non-existent plugin: " + order[ix]);
        }
      } // for
    }
    
    // -- second append plugins which are not listed in sort order
    Iterator it = plugins.values().iterator();
    while (it.hasNext()) {
      PluginIF plugin = (PluginIF) it.next();
      if (orderedPlugins.contains(plugin)) {
        continue;
      }
      // check if matches with group id, if set.
      if (groupId == null ||
          (groupId != null && PluginUtils.inPluginGroups(groupId,
                                                         plugin.getGroups()))) {
        orderedPlugins.add(plugin);
      }
    }
    
    return orderedPlugins;
  }
  
  @Override
  public Collection getPlugins() {
    return getPlugins(null);
  }

  @Override
  public Collection getOrderedPlugins() {
    List orderedPlugins = new ArrayList(plugins.values());
    // sort the titles alphabetically
    Comparator comparator = new PluginComparator();
    Collections.sort(orderedPlugins, comparator);
    return orderedPlugins;
  }
  
  @Override
  public List getPluginGroups() {
    // gather all different groups
    Set groups = new HashSet();
    Iterator it = plugins.values().iterator();
    while (it.hasNext()) {
      PluginIF plugin = (PluginIF) it.next();
      groups.addAll(plugin.getGroups());
    }
    // sort the groups alphabetically
    List allGroups = new ArrayList(groups);
    Comparator comparator = Collator.getInstance();
    Collections.sort(allGroups, comparator);
    return allGroups;
  }
  
  // ------------------------------------------------
  @Override
  public String toString() {
    StringBuilder strBuf = new StringBuilder();
    strBuf.append("NavigatorConfiguration: [")
      .append("autoloads: ").append(autoloads.toString())
      .append(", classmap: ").append(classmap.toString())
      .append(", plugins: ").append(plugins.toString())
      .append(", properties: ").append(properties.toString())
      .append(", models: ").append(mvsConfig.getModels().toString())
      .append(", views: ").append(mvsConfig.getViews().toString())
      .append(", skins: ").append(mvsConfig.getSkins().toString())
      .append("]");
    return strBuf.toString();
  }
  
}
