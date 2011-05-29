
package net.ontopia.topicmaps.nav2.plugins;

import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Plugin that returns the a text snippet.
 */
public class TextPlugin extends DefaultPlugin {

  public String generateHTML(ContextTag context) {
    return getParameter("text");
  }

}
