
// $Id: ParamRuleIF.java,v 1.1 2003/12/22 19:14:36 larsga Exp $

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: A class implementing this interface specifies a
 * transformation rule to modify the forward URL.
 */
public interface ParamRuleIF {

  /**
   * INTERNAL: Transform the action name, based on the values of the
   * given current action and next action or information residing in
   * the context object.
   *
   * @return String containing the manipulated relative request URL
   * with the request parameter value-pairs (based on
   * <code>urlWithParams</code>).
   */
  public String generate(ActionContextIF context,
                         String actionName, String nextActionTemplate,
                         String urlWithParams);

}
