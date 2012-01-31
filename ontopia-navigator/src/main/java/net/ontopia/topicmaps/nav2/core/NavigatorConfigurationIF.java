
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

  public static final String OCCTYPE_METADATA =
    "ontopiaOccurrenceTypeMetadata";
  public static final String DEFVAL_OCCTYPE_METADATA =
    "http://psi.ontopia.net/xtm/occurrence-type/metadata";

  public static final String OCCTYPE_DESCRIPTION =
    "ontopiaOccurrenceTypeDescription";
  public static final String DEFVAL_OCCTYPE_DESCRIPTION =
    "http://psi.ontopia.net/xtm/occurrence-type/description";

  public static final String OCCTYPE_DEFAULT =
    "ontopiaOccurrenceTypeDefault";
  public static final String DEFVAL_OCCTYPE_DEFAULT =
    "http://psi.ontopia.net/xtm/occurrence-type/default";
  
  public static final String DEF_COMPARATOR =
    "defaultComparator";
  public static final String DEFVAL_COMPARATOR =
    "net.ontopia.topicmaps.nav.utils.comparators.TopicComparator";
  
  public static final String DEF_DECIDER =
    "defaultDecider";
  public static final String DEFVAL_DECIDER =
    "net.ontopia.topicmaps.nav2.impl.basic.DefaultIfDecider";
  
  public static final String DEF_CHAR_ENCODING =
    "defaultCharacterEncoding";
  
  public static final String DEF_CONTENT_TYPE =
    "defaultContentType";

  public static final String ALLOW_LOAD_ON_REQUEST =
    "allowLoadOnRequest";

  public static final String MAX_LIST_LENGTH =
    "maxListLength";
  public static final int DEF_VAL_MAX_LIST_LENGTH =
    500;
  
  public static final String DEF_FUNC_ONTRUNCATE =
    "defaultFunctionOnTruncate";

  
  public static final String USERACTION_LOG_LENGTH = 
    "userActionLogLength";
  public static final int DEF_VAL_USERACTION_LOG_LENGTH =
    5;
  
  /**
   * Common base name for those properties specifying the display
   * order of the plugins. Append underscore and plugin group Id to
   * this base name.  For example: "pluginsOrder_topic".
   */
  public static final String PLUGINS_ORDER             = "pluginsOrder";

  public static final String BASENAME_CONTEXT_DECIDER  = "baseNameContextDecider";
  public static final String VARIANT_CONTEXT_DECIDER   = "variantNameContextDecider";
  public static final String OCC_CONTEXT_DECIDER       = "occurrenceContextDecider";
  public static final String ASSOC_CONTEXT_DECIDER     = "associationContextDecider";
  
  public static final String CHECK_FOR_CHANGED_MODULES = "checkForChangedModules";

  public static final String NAMESTRING_NONEXISTENT    = "nameStringNonExistent";
  public static final String NAMESTRING_NULLVALUE      = "nameStringNullValue";
  public static final String NAMESTRING_EMPTYVALUE     = "nameStringEmptyValue";

  public static final String OCCURRENCE_EMPTYVALUE     = "occurrenceEmptyValue";
  public static final String DEFVAL_OCC_EMPTYVALUE     = "[Empty resource string]";
  public static final String OCCURRENCE_EMPTYLOCATOR   = "occurrenceEmptyLocator";
  public static final String DEFVAL_OCC_EMPTYLOC       = "[Empty resource locator]";
  public static final String OCCURRENCE_NULLVALUE      = "occurrenceNullValue";
  public static final String DEFVAL_OCC_NULLVALUE      = "[Null resource string]";
  public static final String OCCURRENCE_NULLLOCATOR    = "occurrenceNullLocator";
  public static final String DEFVAL_OCC_NULLLOC        = "[Null resource locator]";
  public static final String MODULE_READER             = "moduleReader";
  
  // ------------------------------------------------------
  // Property accessors
  // ------------------------------------------------------
  
  /**
   * Get Property value as String for specified name.
   * If property can not be found an empty String is returned.
   */
  public String getProperty(String name);

  /**
   * Get Property value as String for specified name. If
   * property can not be found return <code>defaultValue</code>.
   */
  public String getProperty(String name, String defaultValue);

  /**
   * INTERNAL: Get Property value as int for specified name. If
   * property can not be found return <code>defaultValue</code>.
   *
   * @since 1.4.1
   */
  public int getProperty(String name, int defaultValue);
  
  /**
   * Get all Configuration Properties as a Map
   * containing key (=property-name) - value pairs.
   */
  public Map getProperties();


  // ------------------------------------------------------
  // classmap accessors
  // ------------------------------------------------------
  
  /**
   * Get full-qualified java classname for the
   * specified shortcut name, which can be used by
   * the some tags as attribute value. If shortcut
   * can not be retrieved return empty String.
   */
  public String getClass(String shortcut);

  /**
   * Get Java-Classname Mapping as a Map containing
   * key (=shortcut) - value (=full class name) pairs.
   */
  public Map getClassmap();

  
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
  public boolean isAutoloadTopicMap(String topicmapId);

  /**
   * Returns true if all available topicmaps should be
   * autoloaded at startup time.
   */
  public boolean isAutoloadAllTopicMaps();

  /**
   * Get Set of Topicmaps which are supposed to be
   * autoloaded by application as a <code>Collection</code>
   * of TopicMap ID Strings (used by TopicMapRegistry).
   */
  public Collection getAutoloadTopicMaps();

  
  // ------------------------------------------------------
  // Model-View-Skin accessors
  // ------------------------------------------------------

  /**
   * get storage object for Model/View/Skin settings.
   */
  public MVSConfig getMVSConfig();
  
  /**
   * Get all available models.
   */
  public Collection getModels();

  /**
   * Retrieve the name of the application default model.
   */
  public String getDefaultModel();

  
  /**
   * Get all available views.
   */
  public Collection getViews();

  /**
   * Retrieve the name of the application default view.
   */
  public String getDefaultView();

  
  /**
   * Get all available skins.
   */
  public Collection getSkins();

  /**
   * Retrieve the name of the application default skin.
   */
  public String getDefaultSkin();
  

  // ------------------------------------------------------
  // plugin accessors
  // ------------------------------------------------------

  /**
   * Add a plugin to list of known plugins.
   */
  public void addPlugin(PluginIF aPlugin);

  /**
   * Returns plugin object for specified identifier.
   */
  public PluginIF getPlugin(String id);

  /**
   * Return all plugin objects stored whatever their state (activated,
   * deactivated) is.
   */
  public Collection getPlugins();

  /**
   * Get all plugins independent of their state in alphabetical order.
   */
  public Collection getOrderedPlugins();

  /**
   * Return plugin objects stored whatever their state (activated,
   * deactivated) is that match the specified <code>groupId</code>.
   */
  public Collection getPlugins(String groupId);
    
  /**
   * Return a list of all available plugin groups, that is achieved by
   * looping over all existing plugins and gather all distinct groups..
   */
  public List getPluginGroups();
  
}
