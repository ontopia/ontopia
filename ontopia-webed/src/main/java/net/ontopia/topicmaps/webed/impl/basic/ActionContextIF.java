
// $Id: ActionContextIF.java,v 1.2 2005/09/19 10:12:52 grove Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Map;
import java.util.Collection;
import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * INTERNAL: Stores parameters (key-value pairs: where key is the name
 * of the parameter and value is a String objects)
 */
public interface ActionContextIF {

  /**
   * INTERNAL: Gets the user object who executed the requests and in
   * which authority the consequenctly executed actions run.
   */
  public UserIF getUser();

  /**
   * INTERNAL: Gets all the parameter key value pairs.
   *
   * @return A map containing String object as keys and values.
   */
  public Map getParameters();
  
  /**
   * INTERNAL: Gets the parameter values (as a String array) belonging
   * to the given parameter name.
   */
  public String[] getParameterValues(String paramName);

  /**
   * INTERNAL: Checks that for the given parameter name exactly one
   * value is available and returns this.
   */
  public String getParameterSingleValue(String paramName);
  
  /**
   * INTERNAL: Gets all parameter names stored in this map.
   * Convenience method.
   *
   * @return A collection of String objects.   
   */
  public Collection getParameterNames();

  /**
   * INTERNAL: Returns all the ActionData objects created for this
   * request, whether triggered or not.
   */
  public Collection getAllActions();
}
