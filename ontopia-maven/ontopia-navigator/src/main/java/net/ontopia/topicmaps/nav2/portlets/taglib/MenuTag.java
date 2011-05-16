
// $Id: MenuTag.java,v 1.3 2007/09/18 08:11:37 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.portlets.taglib;

import java.util.List;
import java.util.Collection;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu;

public class MenuTag extends TagSupport {
  private String var;
  private String topic;

  public int doStartTag() throws JspTagException {
    TopicIF topic = (TopicIF) getVariableValue(this.topic);
    if (topic == null)
      throw new JspTagException("Couldn't find topic '" + topic + "'");

    Menu menu = new Menu(topic);
    pageContext.setAttribute(var, menu, PageContext.REQUEST_SCOPE);
    
    return EVAL_BODY_INCLUDE;
  }

//   public int doAfterBody() throws JspTagException {
//     return SKIP_BODY;
//   }

//   public int doEndTag() throws JspException {
//     return EVAL_PAGE;
//   }

  public void release() {
  }

  private boolean isEmpty(String value) {
    return (value == null || value.trim().equals(""));
  }

  // --- Setters

  public void setVar(String var) {
    if (isEmpty(var))
      this.var = null;
    else
      this.var = var;
  }

  public void setTopic(String topic) {
    if (isEmpty(topic))
      this.topic = null;
    else
      this.topic = topic;
  }
  
  // --- Internal

  private Object getVariableValue(String var) {
    // first try to access an OKS variable
    try {
      Collection coll;
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

      if (contextTag != null) {
        coll = contextTag.getContextManager().getValue(var);
        // FIXME: what if it's empty?
        return coll.iterator().next();
      }
    } catch (VariableNotSetException e) {
      // this is OK; we just move on to trying the page context
    }
    
    return InteractionELSupport.getValue(var, pageContext);
  }

}
