
// $Id: ParamRuleChoose2Add.java,v 1.4 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor;

import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionContextIF;

/**
 * INTERNAL: Implementation of ParamRuleIF for rewriting the name of
 * the next 'add' action (with an example name pattern like
 * <code>add_occurrence_{occId}_type</code>) from the current 'choose'
 * action (for example <code>choose_occurrence_3241_type</code>, with
 * the object ids filled in).
 */
public class ParamRuleChoose2Add implements ParamRuleIF {

  // TODO: would be better to extract this somehow from the configuration
  public static final String ADD_ACTION          = "add_";
  public static final String CHOOSE_ACTION       = "choose_";
  public static final String ASSOC_ADD_ACTION    = "assoc_add_";
  public static final String ASSOC_CHOOSE_ACTION = "assoc_choose_";
  
  public String generate(ActionContextIF context,
                         String currentAction, String nextActionTempl,
                         String urlWithParams) {
    StringBuffer result = new StringBuffer(urlWithParams);
    String nextAction = "";
    if (nextActionTempl.startsWith(ADD_ACTION)
        && currentAction.startsWith(CHOOSE_ACTION)) {
      nextAction = StringUtils.replace(currentAction, CHOOSE_ACTION, ADD_ACTION);
    } else if
      (nextActionTempl.startsWith(ASSOC_ADD_ACTION)
       && currentAction.startsWith(ASSOC_CHOOSE_ACTION)) {
      nextAction = StringUtils.replace(currentAction, ASSOC_CHOOSE_ACTION,
                                   ASSOC_ADD_ACTION);
    } else {
      nextAction = nextActionTempl;
    }

    result.append("&").append(Constants.RP_NEXTACTION)
      .append("=").append(nextAction);
    
    return result.toString();
  }

}
