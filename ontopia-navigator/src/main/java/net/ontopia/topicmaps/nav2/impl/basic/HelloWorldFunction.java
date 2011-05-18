
// $Id: HelloWorldFunction.java,v 1.8 2003/12/18 17:03:18 larsga Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * INTERNAL: Implementation of FunctionIF interface for testing
 * purposes. This class may also be used to demonstrate also the
 * external function mechansimn.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.ExternalFunctionTag
 */
public class HelloWorldFunction extends AbstractFunction {
  
  public Collection execute(PageContext pageContext, TagSupport callingTag)
    throws IOException, JspException {
    pageContext.getOut().print("Hello World!\n");
    return null;
  }
  
}
