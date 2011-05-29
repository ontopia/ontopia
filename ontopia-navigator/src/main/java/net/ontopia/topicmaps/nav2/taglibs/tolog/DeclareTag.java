
package net.ontopia.topicmaps.nav2.taglibs.tolog;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Tolog Tag for making a set of tolog declarations
 * available within the nearest ancestor ContextTag.
 */
public class DeclareTag extends BodyTagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(DeclareTag.class.getName());
  
  protected String declarations;
  
  /**
   * Default constructor.
   */
  public DeclareTag() {
    super();
  }
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    return EVAL_BODY_BUFFERED;
  }
  
  /** 
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    declarations = getBodyContent().getString();
    return SKIP_BODY;
  }
  
  /**
   * Process the end tag.
   */
  public int doEndTag() throws JspException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null)
      throw new JspTagException("<tolog:declare> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    
    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("DeclareTag found no "
              + "topic map.");

    DeclarationContextIF declarationContext = contextTag
            .getDeclarationContext();
    try {
      declarationContext = QueryUtils.parseDeclarations(topicmap, declarations,
              declarationContext);
      
    } catch (InvalidQueryException e) {
      throw new JspTagException(e.getMessage());
    }
    contextTag.setDeclarationContext(declarationContext);
                  
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    super.release();
  }
}
