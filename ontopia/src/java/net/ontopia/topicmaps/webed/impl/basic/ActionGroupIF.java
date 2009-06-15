
// $Id: ActionGroupIF.java,v 1.2 2005/03/10 08:26:55 grove Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.List;
import java.util.Map;

/**
 * INTERNAL: The interface is implemented by objects representing
 * action groups that handle and provide access to a collection of
 * actions.
 */
public interface ActionGroupIF {

  /**
   * INTERNAL: Gets the name of this action group.
   */
  public String getName();
  
  /**
   * INTERNAL: Add an action to this action group. Note that the order
   * in which actions are added is significant.
   */
  public void addAction(ActionInGroup action);

  /**
   * INTERNAL: Returns all actions that are assigned to this action
   * group in the order in which they are to be executed.
   *
   * @return a list of ActionInGroup objects
   */
  public List getActions();

  /**
   * INTERNAL: Gets the action by the given name.
   */
  public ActionInGroup getAction(String name);

  /**
   * INTERNAL: Sets the default forward page for the given action
   * response type.
   */
  public void setDefaultForwardPage(int responseType,
                                    ActionForwardPageIF forwardPage);

  /**
   * INTERNAL: Gets the forward page which should be used as default for
   * the given action response type. If there is no specific forward
   * page for the given response type tries to retrieve the generic
   * forward page, otherwise null.
   */
  public ActionForwardPageIF getDefaultForwardPage(int responseType);

  /**
   * INTERNAL: Gets all default forward pages assigned to this action
   * group.
   *
   * @return A map containing the action response type (as Integer
   *         object, see {@link ActionResponseIF} constants) as key
   *         and the corresponding {@link ActionForwardPageIF}
   *         object.
   */
  public Map getDefaultForwardPages();
  
  /**
   * INTERNAL: Sets the forward page in the case a lock situation
   */
  public void setLockedForwardPage(ActionForwardPageIF forwardPage);

  /**
   * INTERNAL: Gets the forward page to use when locking fails.
   */
  public ActionForwardPageIF getLockedForwardPage();
  
  /**
   * INTERNAL: Sets forward page for the given pair of action and action
   * response type.
   */
  public void setForwardPage(ActionInGroup action,
                             int responseType,
                             ActionForwardPageIF forwardPage);

  /**
   * INTERNAL: Gets the forward page for the given action and response
   * type. The following order has to be implemented for resolving an
   * appropiate forward page:
   * <ul>
   *  <li>try to get forward page for specified action and response type,</li>
   *  <li>if not found: try to get generic forward page for given action,</li>
   *  <li>if not found: try to get default forward page for given response
   *      type,</li>
   *  <li>if not found: try to get generic default forward page,</li>
   *  <li>if not found: return null.</li>
   * </ul>
   *
   * @param error whether there was an error or not.
   */
  public ActionForwardPageIF getForwardPage(ActionInGroup action, 
                                            boolean error);

  /**
   * INTERNAL: Gets all forward pages assigned to this action group.
   *
   * @return A map containing the action response (as
   *         ActionResponseComposite object) as key and the
   *         corresponding ActionForwardPageIF object.
   */
  public Map getForwardPages();
  
}
