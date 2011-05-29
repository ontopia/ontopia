
package net.ontopia.topicmaps.nav.taglibs.template;

import java.util.Map;
import java.util.Stack;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Code-sharing abstract superclass for GetTag and PutTag.
 */
public abstract class AbstractTemplateTag extends BodyTagSupport {
  protected String name;

  protected void putParameter(String content, boolean direct) throws JspException {
    
    InsertTag parent = (InsertTag) findAncestorWithClass(this, InsertTag.class);
    if (parent == null)
      throw new JspException(this.getClass().getName() + ": has no template:insert ancestor.");

    Stack template_stack = parent.getStack();
    if (template_stack == null) 
      throw new JspException(this.getClass().getName() + ": has no template stack."); 

    Map params = (Map) template_stack.peek();
    if (params == null) 
      throw new JspException(this.getClass().getName() + ": has no parameter map.");      
                
    params.put(name, new PageParameter(content, direct));
  }
  
  protected PageParameter getParameter() throws JspException, NavigatorRuntimeException {
    Stack stack = (Stack) pageContext
      .getAttribute(InsertTag.TEMPL_STACK_KEY, PageContext.REQUEST_SCOPE);
    if (stack == null)
      throw new JspException(this.getClass().getName() + " has no template stack.");

    Map params = (Map) stack.peek();
    if (params == null)
      throw new JspException(this.getClass().getName() + " has no parameter map.");
                        
    return (PageParameter) params.get(name);
  }
}
