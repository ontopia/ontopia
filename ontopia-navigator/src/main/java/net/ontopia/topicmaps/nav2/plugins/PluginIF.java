
// $Id: PluginIF.java,v 1.7 2007/07/13 12:35:06 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.plugins;

import java.util.List;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/** 
 * INTERNAL: The common interface for all navigator plugin objects.
 * This interface can either be implemented by the plugin, or a
 * default implementation (DefaultPlugin) can be used. 
 */
public interface PluginIF {

  public static final int ACTIVATED = 0;
  public static final int DEACTIVATED = 1;
  public static final int ERROR = 2;
  
  /**
   * INTERNAL: Called by the framework to make the plugin produce the
   * HTML that is going to represent it on a web page in the web
   * application.
   *
   * @return An HTML string to be written into the page. If the returned
   *         string is null it means that the plugin does not wish to be
   *         displayed on this page.
   */
  public String generateHTML(ContextTag context);
  
  /**
   * INTERNAL: Called by the framework to finalize initialization.
   * Called when there are no more parameters.
   */
  public void init();

  
  // ----------------------------------------------------------
  // Accessor methods
  // ----------------------------------------------------------
  
  /**
   * INTERNAL: Returns the ID of this plugin.
   */
  public String getId();

  /**
   * INTERNAL: Sets the ID of this plugin.
   */
  public void setId(String id);

  /**
   * INTERNAL: Returns the groups this plugin belongs to.  Each group is
   * represented by a string containing the group id.
   */
  public List getGroups();

  /**
   * INTERNAL: Reset all group settings for this plugin. After this
   * operation this plugin will belong to no group.
   */
  public void resetGroups();

  /**
   * INTERNAL: Add the specified group to groups this plugin belongs to.
   */
  public void addGroup(String groupId);
    
  /**
   * INTERNAL: Sets the groups this plugin belongs to.
   */
  public void setGroups(List groups);

  /**
   * INTERNAL: Returns the title of this plugin.
   */
  public String getTitle();

  /**
   * INTERNAL: Sets the title of this plugin.
   */
  public void setTitle(String title);

  /**
   * INTERNAL: Gets the description of this plugin.
   */
  public String getDescription();

  /**
   * INTERNAL: Sets the description of this plugin.
   */
  public void setDescription(String description);

  /**
   * INTERNAL: Returns the URI of this plugin.
   */
  public String getURI();

  /**
   * INTERNAL: Sets the URI of this plugin.
   * <p>
   * Note: This has not to contain the web application
   * context path.
   * <p>
   * Example: <code>plugins/hello/hello.jsp</code>
   */
  public void setURI(String uri);

  /**
   * INTERNAL: Returns the URI frame target of this plugin.
   */
  public String getTarget();

  /**
   * INTERNAL: Sets the URI frame target of this plugin.
   */
  public void setTarget(String target);

  /**
   * INTERNAL: Returns the state of this plugin.
   */
  public int getState();

  /**
   * INTERNAL: Sets the state of this plugin.
   */
  public void setState(int state);
  
  /**
   * INTERNAL: Returns the value of the parameter.
   */
  public String getParameter(String name);
  
  /**
   * INTERNAL: Called by the framework to give the plugin the value of a
   * configuration parameter.
   */
  public void setParameter(String name, String value);

  /**
   * INTERNAL: Returns the path to the plugin directory. This is the
   * path in the file system the server is running in, if the web
   * application is deployed as an unexploded WAR.
   */
  public String getPluginDirectory();
 
  /**
   * INTERNAL: Called by the framework to give the plugin the directory
   * its plugin.xml file was found in. This is useful for plugins
   * which wish to use other files found in the same directory.
   */
  public void setPluginDirectory(String path);
  
}
