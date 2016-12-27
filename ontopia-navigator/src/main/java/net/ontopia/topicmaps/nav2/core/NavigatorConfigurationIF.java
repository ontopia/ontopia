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

package net.ontopia.topicmaps.nav2.core;

import java.util.Collection;
import java.util.Map;
import java.util.List;

import net.ontopia.topicmaps.nav2.impl.framework.MVSConfig;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;

/**
 * INTERNAL: Implemented by an object which stores configuration
 * information needed by the navigator.
 */
public interface NavigatorConfigurationIF {

  // ---------------------------------------------------------
  // Property name constants
  // ---------------------------------------------------------
  // Constants defining property names used in the application
  // configuration XML specification file (application.xml).
  // Note: For further information please read the comments
  // inside the config file application.xml.
  // ---------------------------------------------------------

  String OCCTYPE_METADATA =
    "ontopiaOccurrenceTypeMetadata";
  String DEFVAL_OCCTYPE_METADATA =
    "http://psi.ontopia.net/xtm/occurrence-type/metadata";

  String OCCTYPE_DESCRIPTION =
    "ontopiaOccurrenceTypeDescription";
  String DEFVAL_OCCTYPE_DESCRIPTION =
    "http://psi.ontopia.net/xtm/occurrence-type/description";

  String OCCTYPE_DEFAULT =
    "ontopiaOccurrenceTypeDefault";
  String DEFVAL_OCCTYPE_DEFAULT =
    "http://psi.ontopia.net/xtm/occurrence-type/default";
  
  String DEF_COMPARATOR =
    "defaultComparator";
  String DEFVAL_COMPARATOR =
    "net.ontopia.topicmaps.nav.utils.comparators.TopicComparator";
  
  String DEF_DECIDER =
    "defaultDecider";
  String DEFVAL_DECIDER =
    "net.ontopia.topicmaps.nav2.impl.basic.DefaultIfDecider";
  
  String DEF_CHAR_ENCODING =
    "defaultCharacterEncoding";
  
  String DEF_CONTENT_TYPE =
    "defaultContentType";

  String ALLOW_LOAD_ON_REQUEST =
    "allowLoadOnRequest";

  String MAX_LIST_LENGTH =
    "maxListLength";
  int DEF_VAL_MAX_LIST_LENGTH =
    500;
  
  String DEF_FUNC_ONTRUNCATE =
    "defaultFunctionOnTruncate";

  
  String USERACTION_LOG_LENGTH = 
    "userActionLogLength";
  int DEF_VAL_USERACTION_LOG_LENGTH =
    5;
  
  /**
   * Common base name for those properties specifying the display
   * order of the plugins. Append underscore and plugin group Id to
   * this base name.  For example: "pluginsOrder_topic".
   */
  String PLUGINS_ORDER             = "pluginsOrder";

  String BASENAME_CONTEXT_DECIDER  = "baseNameContextDecider";
  String VARIANT_CONTEXT_DECIDER   = "variantNameContextDecider";
  String OCC_CONTEXT_DECIDER       = "occurrenceContextDecider";
  String ASSOC_CONTEXT_DECIDER     = "associationContextDecider";
  
  String CHECK_FOR_CHANGED_MODULES = "checkForChangedModules";

  String NAMESTRING_NONEXISTENT    = "nameStringNonExistent";
  String NAMESTRING_NULLVALUE      = "nameStringNullValue";
  String NAMESTRING_EMPTYVALUE     = "nameStringEmptyValue";

  String OCCURRENCE_EMPTYVALUE     = "occurrenceEmptyValue";
  String DEFVAL_OCC_EMPTYVALUE     = "[Empty resource string]";
  String OCCURRENCE_EMPTYLOCATOR   = "occurrenceEmptyLocator";
  String DEFVAL_OCC_EMPTYLOC       = "[Empty resource locator]";
  String OCCURRENCE_NULLVALUE      = "occurrenceNullValue";
  String DEFVAL_OCC_NULLVALUE      = "[Null resource string]";
  String OCCURRENCE_NULLLOCATOR    = "occurrenceNullLocator";
  String DEFVAL_OCC_NULLLOC        = "[Null resource locator]";
  String MODULE_READER             = "moduleReader";
  
  // ------------------------------------------------------
  // Property accessors
  // ------------------------------------------------------
  
  /**
   * Get Property value as String for specified name.
   * If property can not be found an empty String is returned.
   */
  String getProperty(String name);

  /**
   * Get Property value as String for specified name. If
   * property can not be found return <code>defaultValue</code>.
   */
  String getProperty(String name, String defaultValue);

  /**
   * INTERNAL: Get Property value as int for specified name. If
   * property can not be found return <code>defaultValue</code>.
   *
   * @since 1.4.1
   */
  int getProperty(String name, int defaultValue);
  
  /**
   * Get all Configuration Properties as a Map
   * containing key (=property-name) - value pairs.
   */
  Map getProperties();


  // ------------------------------------------------------
  // classmap accessors
  // ------------------------------------------------------
  
  /**
   * Get full-qualified java classname for the
   * specified shortcut name, which can be used by
   * the some tags as attribute value. If shortcut
   * can not be retrieved return empty String.
   */
  String getClass(String shortcut);

  /**
   * Get Java-Classname Mapping as a Map containing
   * key (=shortcut) - value (=full class name) pairs.
   */
  Map getClassmap();

  
  // ------------------------------------------------------
  // autoload topicmaps accessors
  // ------------------------------------------------------
  
  /**
   * Returns true if specified TopicMap ID (used by TopicMapRegistry)
   * should be autoloaded at startup or can be loaded
   * afterwards by request otherwise false.
   *
   * @see net.ontopia.topicmaps.entry.TopicMapRepositoryIF
   */
  boolean isAutoloadTopicMap(String topicmapId);

  /**
   * Returns true if all available topicmaps should be
   * autoloaded at startup time.
   */
  boolean isAutoloadAllTopicMaps();

  /**
   * Get Set of Topicmaps which are supposed to be
   * autoloaded by application as a <code>Collection</code>
   * of TopicMap ID Strings (used by TopicMapRegistry).
   */
  Collection getAutoloadTopicMaps();

  
  // ------------------------------------------------------
  // Model-View-Skin accessors
  // ------------------------------------------------------

  /**
   * get storage object for Model/View/Skin settings.
   */
  MVSConfig getMVSConfig();
  
  /**
   * Get all available models.
   */
  Collection getModels();

  /**
   * Retrieve the name of the application default model.
   */
  String getDefaultModel();

  
  /**
   * Get all available views.
   */
  Collection getViews();

  /**
   * Retrieve the name of the application default view.
   */
  String getDefaultView();

  
  /**
   * Get all available skins.
   */
  Collection getSkins();

  /**
   * Retrieve the name of the application default skin.
   */
  String getDefaultSkin();
  

  // ------------------------------------------------------
  // plugin accessors
  // ------------------------------------------------------

  /**
   * Add a plugin to list of known plugins.
   */
  void addPlugin(PluginIF aPlugin);

  /**
   * Returns plugin object for specified identifier.
   */
  PluginIF getPlugin(String id);

  /**
   * Return all plugin objects stored whatever their state (activated,
   * deactivated) is.
   */
  Collection getPlugins();

  /**
   * Get all plugins independent of their state in alphabetical order.
   */
  Collection getOrderedPlugins();

  /**
   * Return plugin objects stored whatever their state (activated,
   * deactivated) is that match the specified <code>groupId</code>.
   */
  Collection getPlugins(String groupId);
    
  /**
   * Return a list of all available plugin groups, that is achieved by
   * looping over all existing plugins and gather all distinct groups..
   */
  List getPluginGroups();
  
}
