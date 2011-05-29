
package net.ontopia.topicmaps.nav2.taglibs.logic;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;

/**
 * INTERNAL: Defines the names and types of variables used by the
 * <code>ContextTag</code>. This makes it possible to access the
 * variables within the JSP.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag
 */
public class ContextExtraInfo extends TagExtraInfo {

  public VariableInfo[] getVariableInfo(TagData data) {
    return new VariableInfo[] {
      new VariableInfo(NavigatorApplicationIF.NAV_APP_KEY,
                       "net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF",
                       true, VariableInfo.NESTED),
      new VariableInfo(NavigatorApplicationIF.CONTEXT_KEY,
                       "net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag",
                       true, VariableInfo.NESTED)
    };
  }
  
}
