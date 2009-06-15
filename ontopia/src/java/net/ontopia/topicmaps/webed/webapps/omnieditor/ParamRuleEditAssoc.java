
// $Id: ParamRuleEditAssoc.java,v 1.3 2003/12/22 19:16:09 larsga Exp $

package net.ontopia.topicmaps.webed.webapps.omnieditor;

import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.ActionContextIF;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;

/**
 * INTERNAL: Implementation of ParamRuleIF for extracting the
 * association ID from a request parameter like
 * <code>edit_assoc_12345</code>.
 */
public class ParamRuleEditAssoc implements ParamRuleIF {

  // TODO: generalize this a bit more with the help of configuration file
  //       or central stored constants
  public static final String EDIT_ASSOC_START = "edit_assoc_";
  
  public String generate(ActionContextIF context,
                         String currentAction, String nextActionTempl,
                         String urlWithParams) {
    StringBuffer result = new StringBuffer(urlWithParams);
    String nextAction = "";
    if (currentAction.startsWith(EDIT_ASSOC_START)) {
      nextAction = StringUtils.replace(currentAction, EDIT_ASSOC_START, "");
    } else {
      nextAction = currentAction;
    }

    result.append("&").append(Constants.RP_NEXTACTION)
      .append("=").append(nextAction);
    
    return result.toString();
  }

}
