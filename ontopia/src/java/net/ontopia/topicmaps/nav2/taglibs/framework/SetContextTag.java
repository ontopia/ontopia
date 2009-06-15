
// $Id: SetContextTag.java,v 1.13 2008/06/12 14:37:19 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav.context.UserFilterContextStore;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.core.TopicMapIF;

import org.apache.log4j.Logger;

/**
 * INTERNAL: ...
 */
public class SetContextTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = Logger
    .getLogger(SetContextTag.class.getName());
  
  // tag attributes
  private String basenameValue;
  private String variantValue;
  private String occurrenceValue;
  private String associationValue;

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    UserIF user = FrameworkUtils.getUser(contextTag.getPageContext());
    UserFilterContextStore userContext = user.getFilterContext();
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("SetContextTag found no topic map.");

    if (userContext == null) 
      userContext = new UserFilterContextStore();

    Collection themes = getvalue(contextTag, basenameValue);
    if (themes != null)
      userContext.setScopeTopicNames(topicmap, themes);
    
    themes = getvalue(contextTag, variantValue);
    if (themes != null)
      userContext.setScopeVariantNames(topicmap, themes);
    
    themes = getvalue(contextTag, occurrenceValue);
    if (themes != null)
      userContext.setScopeOccurrences(topicmap, themes);
    
    themes = getvalue(contextTag, associationValue);
    if (themes != null)
      userContext.setScopeAssociations(topicmap, themes);

    return SKIP_BODY;
  }

  /**
   * Overrides the parent method.
   */
  public void release() {
    // does nothing
  }
  
  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  public void setBasename(String var) {
    basenameValue = var;
  }

  public void setVariant(String var) {
    variantValue = var;
  }

  public void setOccurrence(String var) {
    occurrenceValue = var;
  }

  public void setAssociation(String var) {
    associationValue = var;
  }

  // --- internal methods

  private Collection getvalue(ContextTag contextTag, String token) {
    if (token != null) {
      if (token.equalsIgnoreCase("none"))
        return Collections.EMPTY_LIST;
      else 
        return contextTag.getContextManager().getValue(token);
    } else
      return null;
  }
}
