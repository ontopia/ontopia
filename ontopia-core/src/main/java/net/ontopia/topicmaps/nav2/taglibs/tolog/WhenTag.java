
// $Id: WhenTag.java,v 1.4 2004/11/29 10:11:45 opland Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Variant of IfTag, which sets a parent alerts a parent ChooseTag,
 * if it's body is evaluated.
 * If the body is evaluated parentChooser.setFoundMatchingWhen() is called.
 */
public class WhenTag extends IfTag {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(WhenTag.class.getName());

  // members
  protected ChooseTag parentChooser;
  
  /**
   * Default constructor.
   */
  public WhenTag() {
    super();
  }
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    parentChooser = (ChooseTag) findAncestorWithClass(this, ChooseTag.class);
    if (parentChooser == null)
      throw new JspTagException(
              "tolog:when tag is not inside tolog:choose tag.");

    parentChooser.setFoundWhen();
    
    // If a matching when was already found within the parentChooser
    if (parentChooser.foundMatchingWhen()) {
      // No more WhenTags need to be executed (tested in each WhenTag).
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
      if (contextTag == null)
        throw new JspTagException("<tolog:when> must be nested directly or"
                + " indirectly within a <tolog:context> tag, but no"
                + " <tolog:context> tag was found.");
      
      contextManager = contextTag.getContextManager();
      
      contextManager.pushScope();
      return SKIP_BODY;
    }
    
    return super.doStartTag();
  }

  /** 
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    parentChooser.setFoundMatchingWhen();
    
    return super.doAfterBody();
  }
  
  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // reset members
    parentChooser = null;

    super.release();
  }
}
