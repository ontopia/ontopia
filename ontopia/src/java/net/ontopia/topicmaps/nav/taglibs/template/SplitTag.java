
// $Id: SplitTag.java,v 1.5 2004/11/28 13:45:57 larsga Exp $

package net.ontopia.topicmaps.nav.taglibs.template;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;

/**
 * INTERNAL: Tag used to indicate where a nested template is to be split.
 *
 * @since 2.0
 */
public class SplitTag extends TagSupport {
  public static final String TOKEN =
    "^|~---------net.ontopia.topicmaps.nav.taglibs.template.SplitTag--------~|^";
  
  public int doStartTag() throws JspException {
    PutTag parent = (PutTag) findAncestorWithClass(this, PutTag.class);
    if (parent == null)
      throw new JspException("Split tag has no template:put ancestor.");

    try {
      pageContext.getOut().print(TOKEN);
    } catch (java.io.IOException e) {
      throw JSPEngineWrapper.getJspException("Error writing split tag token", e);
    }
    
    return SKIP_BODY;
  }

  public int doEndTag() {
    return EVAL_PAGE;
  }

}
