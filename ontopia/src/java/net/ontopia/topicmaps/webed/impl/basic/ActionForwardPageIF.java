
// $Id: ActionForwardPageIF.java,v 1.3 2005/03/15 15:29:30 ian Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Map;

/**
 * INTERNAL: Interface for the description of a forward page assigned
 * to an action.
 */
public interface ActionForwardPageIF {

  /**
   * INTERNAL: Gets the URL to forward to.
   */
  public String getURL();

  /**
   * INTERNAL: Gets the name of the frame which the forwarded page should
   * appear in.
   */
  public String getFramename();

  /**
   * INTERNAL: Adds a request parameter (name, value) pair which should
   * be included when setting up the next request.
   */
  public void addParameter(String paramName, String paramValue);

  /**
   * INTERNAL: Gets all existing request parameter (name, value) pairs.
   *
   * @return Map containing parameter name (String object) as key
   *         and parameter value (String object) as value.
   */
  public Map getParameters();
  
  /**
   * INTERNAL: Gets name of the template for the request parameter which
   * identifies the next action. May be making use of name patterns
   * with the help of placeholders.
   *
   * @see ActionNamePatternIF
   */
  public String getNextActionTemplate();
  
  /**
   * INTERNAL: Get the rule for processing the request parameter
   * handling with the current action name and constructing the next
   * action request parameter with the help of the next action
   * template.
   *
   * <p> Note: If no specific rule was setup the next
   * action will be identical to the next action template
   */
  public ParamRuleIF getNextActionParamRule();
  
}
