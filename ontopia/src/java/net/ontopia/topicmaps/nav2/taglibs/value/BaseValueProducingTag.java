
// $Id: BaseValueProducingTag.java,v 1.18 2004/06/14 10:52:27 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.Collection;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueProducingTagIF;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Abstract super-class of an Value-Producing Tag.
 * <p>
 * Note: Not all value producing tags are manipulating collections,
 * so this is not the base class of all value producing tags.
 * Exceptions are StringTag and ClassesTag.
 */
public abstract class BaseValueProducingTag extends TagSupport
  implements ValueProducingTagIF {

  // tag attributes
  protected String variableName;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspException {
    // retrieve parent tag which accepts the produced collection by this tag 
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);
    if (acceptingTag == null)
      throw new NavigatorRuntimeException(getClass().getName() +
                                          "couldn't find value-accepting ancestor tag to pass value to!");

    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    ContextManagerIF ctxtMgr = contextTag.getContextManager();

    // get collection on which we should compute 
    Collection inputCollection = getInputCollection(ctxtMgr);

    // collection processing
    Collection resultCollection = process( inputCollection );

    // kick it up to the accepting tag
    acceptingTag.accept( resultCollection );

    // ignore body if variable name is set
    if (variableName != null)
      return SKIP_BODY;
    else
      return EVAL_BODY_INCLUDE;
  }

  /**
   * Processes the end tag.
   */
  public int doEndTag() throws JspException {
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // override default behaviour
    // do not set parent to null!!!
  }
  
  /**
   * INTERNAL: return collection to work with, throw
   * <code>NavigatorRuntimeException</code> if collection is null.
   */
  protected Collection getInputCollection(ContextManagerIF ctxtMgr)
    throws NavigatorRuntimeException {
    if (variableName == null)
      return ctxtMgr.getDefaultValue();
    else
      return ctxtMgr.getValue(variableName);
  }
  
  // -----------------------------------------------------------------
  // set method(s) for the tag attributes
  // -----------------------------------------------------------------

  public void setOf(String variableName) {
    this.variableName = variableName;
  }
  
}
