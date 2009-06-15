
// $Id: ShowActionGroupParamRule.java,v 1.3 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor;

import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionContextIF;

import org.apache.oro.text.perl.Perl5Util;

/**
 * INTERNAL: Implementation of ParamRuleIF to substitute the show
 * action group request parameter name with the action group and to
 * remove the action group request parameter.
 *
 * This is for example useful for the simple submit action in the tab
 * of the topic edit page.
 */
public class ShowActionGroupParamRule implements ParamRuleIF {

  // share this instance with all objects
  static Perl5Util putil = new Perl5Util();
  
  public String generate(ActionContextIF context,
                         String actionName, String nextActionTemplate,
                         String urlWithParams) {
    String url = urlWithParams;
    // ag=bar -> REMOVE
    url = putil.substitute("s/(\\?|&)" + Constants.RP_ACTIONGROUP +
                           "=.*?(&|$)/$1/", url);
    // show_ag=foo -> ag=foo
    url = putil.substitute("s/(\\?|&)" + Constants.RP_SHOW_AG +
                           "=(.*?)(&|$)/$1" + Constants.RP_ACTIONGROUP +
                           "=$2$3/", url);
    return url;
  }
  
}
