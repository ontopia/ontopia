
// $Id: GetContextTag.java,v 1.13 2008/06/12 14:37:19 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ValueAcceptingTagIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.core.TopicMapIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The implementation of <framework:getcontext>.
 */
public class GetContextTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(GetContextTag.class.getName());
  
  // tag attributes
  private String context;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // retrieve parent tag which accepts the result of this value producing op.
    ValueAcceptingTagIF acceptingTag = (ValueAcceptingTagIF)
      findAncestorWithClass(this, ValueAcceptingTagIF.class);

    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    UserFilterContextStore userContext = user.getFilterContext();
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("GetContextTag found no topic map.");

    if (userContext == null)
      userContext = new UserFilterContextStore();

    Collection result = Collections.EMPTY_SET;
    if (context != null) {
      if (context.equals("basename"))
        result = userContext.getScopeTopicNames(topicmap);
      else if (context.equals("variant"))
        result = userContext.getScopeVariantNames(topicmap);
      else if (context.equals("association"))
        result = userContext.getScopeAssociations(topicmap);
      else if (context.equals("occurrence"))
        result = userContext.getScopeOccurrences(topicmap);
    }

    // kick it over to the accepting tag
    acceptingTag.accept(result);      

    return SKIP_BODY;
  }
  
  /**
   * Overrides the parent method.
   */
  public void release() {
    // does nothing
  }
  
  public void setContext(String var) throws NavigatorRuntimeException {
    context = var;
    if (!context.equals("basename") &&
        !context.equals("variant") &&
        !context.equals("occurrence") &&
        !context.equals("association"))
      throw new NavigatorRuntimeException("Incorrect value ('" + var + "')" +
                                          " given for attribute 'context' in" +
                                          " element 'getcontext'.");
  }

}
