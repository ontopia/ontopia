
// $Id: WebEdRequestIF.java,v 1.4 2005/01/12 16:29:30 larsga Exp $

package net.ontopia.topicmaps.webed.core;

import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * PUBLIC: Represents a request to a web editor framework application.
 *
 * @since 2.0
 */
public interface WebEdRequestIF {
  
  /**
   * PUBLIC: Returns the parameters of the named action.
   */
  public ActionParametersIF getActionParameters(String name);

  /**
   * PUBLIC: Returns the user object connected with this request.
   */
  public UserIF getUser();

  /**
   * PUBLIC: Returns true if at least one action has already been run
   * in this request.
   * @since 2.1.1
   */
  public boolean getActionsExecuted();
}
