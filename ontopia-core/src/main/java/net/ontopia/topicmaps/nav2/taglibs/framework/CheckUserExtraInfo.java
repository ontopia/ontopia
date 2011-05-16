
// $Id: CheckUserExtraInfo.java,v 1.7 2004/01/28 19:10:05 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.framework;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;

/**
 * INTERNAL: Defines the names and types of variables
 * used by the <code>CheckUserTag</code>.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.framework.CheckUserTag
 */
public class CheckUserExtraInfo extends TagExtraInfo {

  public VariableInfo[] getVariableInfo(TagData data) {
    return new VariableInfo[] {
      new VariableInfo(NavigatorApplicationIF.USER_KEY,
                       "net.ontopia.topicmaps.nav2.core.UserIF",
                       true, VariableInfo.AT_END)
    };
  }
  
}
