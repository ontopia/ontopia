
// $Id: ShowActionGroupParamRule.java,v 1.3 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionContextIF;

/**
 * INTERNAL: Implementation of ParamRuleIF to substitute the show
 * action group request parameter name with the action group and to
 * remove the action group request parameter.
 *
 * This is for example useful for the simple submit action in the tab
 * of the topic edit page.
 */
public class ShowActionGroupParamRule implements ParamRuleIF {

  private static final Pattern PATTERN_AG = Pattern.compile("(\\?|&)(" + Constants.RP_ACTIONGROUP + "=.*?(&|$))");
  private static final Pattern PATTERN_SHOW = Pattern.compile("(\\?|&)" + Constants.RP_SHOW_AG + "=(.*?)(&|$)");
  
  public String generate(ActionContextIF context,
                         String actionName, String nextActionTemplate,
                         String urlWithParams) {
    String url = urlWithParams;
    // ag=bar -> REMOVE
    Matcher m = PATTERN_AG.matcher(url);
    if (m != null) {
      url = m.replaceAll("$1");
    }
    // show_ag=foo -> ag=foo
    m = PATTERN_SHOW.matcher(url);
    if (m != null) {
      url = m.replaceAll("$1" + Constants.RP_ACTIONGROUP + "=$2$3");
    }
    return url;
  }
  
}
