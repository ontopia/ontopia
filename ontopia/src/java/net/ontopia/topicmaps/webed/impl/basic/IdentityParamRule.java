
// $Id: IdentityParamRule.java,v 1.8 2005/09/14 07:22:10 grove Exp $

package net.ontopia.topicmaps.webed.impl.basic;


/**
 * INTERNAL: Implementation of ParamRuleIF for returning the next
 * action template name. No Processing is done.
 */
public class IdentityParamRule implements ParamRuleIF {

  /**
   * @return urlWithParams
   */
  public String generate(ActionContextIF context,
                         String currentAction, String nextActionTempl,
                         String urlWithParams) {
    return urlWithParams;
  }

}
