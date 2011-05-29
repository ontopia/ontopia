
package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ExpressionEvaluator;

/**
 * INTERNAL: Fake the PageContext, needed for execution of a JSP.
 */
public class FakePageContext extends PageContext {
  private Map attrs;
  private ServletRequest request;
  private JspWriter out;
  private Stack writerStack = new Stack();
  private ServletContext context;
  private ServletConfig config;
  private HttpSession session;
  
  public FakePageContext(Writer out) {
    this(out, new HashMap());
  }

  public FakePageContext(Writer out, Map attrs) {
    this(out, attrs, new HashMap(), ".");
  }
  
  public FakePageContext(Writer out, Map attrs, Map params, String path) {
    this.attrs = attrs;
    this.out = new DefaultJspWriter(out);

    // set up default environment
    request = new FakeServletRequest(params, attrs);
    context = new FakeServletContext(path);
    config = new FakeServletConfig(context);
    session = new FakeHttpSession(context);
  }

  // -- internal mutators

  public void setAttributes(Map attrs) {
    this.attrs = attrs;
  }
  
  public void setRequest(ServletRequest request) {
    this.request = request;
  }

  public void setServletConfig(ServletConfig config) {
    this.config = config;
  }

  public void setSession(HttpSession session) {
    this.session = session;
  }
  
  // --
  
  public void initialize(Servlet servlet, ServletRequest request,
                         ServletResponse response, String errorPageURL,
                         boolean needsSession, int bufferSize,
                         boolean autoFlush)
    throws IOException, IllegalStateException, IllegalArgumentException {
    throw new UnsupportedOperationException();
    // TODO
  }

  public void release() {
    // TODO
  }

  public Object getAttribute(String name) {
    return attrs.get(name);
  }

  public Object getAttribute(String name, int scope) {
    switch (scope) {
    case PageContext.APPLICATION_SCOPE:
      return getServletContext().getAttribute(name);
    case PageContext.REQUEST_SCOPE:
      return getRequest().getAttribute(name);
    case PageContext.SESSION_SCOPE:
      return getSession().getAttribute(name);
    case PageContext.PAGE_SCOPE:
      return getAttribute(name);
    default:
      throw new IllegalArgumentException("Illegal scope argument: " + scope);
    }
  }

  public void setAttribute(String name, Object value) {
    if (value == null)
      // JSP spec doesn't allow nulls
      throw new NullPointerException("Null value not allowed");
    else 
      attrs.put(name, value);
  }

  public void setAttribute(String name, Object value, int scope) {
    if (value == null) 
      // JSP spec doesn't allow nulls
      throw new NullPointerException("Null value not allowed");

    switch (scope) {
    case PageContext.APPLICATION_SCOPE:
      getServletContext().setAttribute(name, value);
      break;
    case PageContext.REQUEST_SCOPE:
      getRequest().setAttribute(name, value);
      break;
    case PageContext.SESSION_SCOPE:
      getSession().setAttribute(name, value);
      break;
    case PageContext.PAGE_SCOPE:
      attrs.put(name, value);
      break;
    default:
      throw new IllegalArgumentException("Illegal scope argument: " + scope);
    }
  }

  public void removeAttribute(String name, int scope) {
    switch (scope) {
    case PageContext.APPLICATION_SCOPE:
      getServletContext().removeAttribute(name);
      break;
    case PageContext.REQUEST_SCOPE:
      getRequest().removeAttribute(name);
      break;
    case PageContext.SESSION_SCOPE:
      getSession().removeAttribute(name);
      break;
    case PageContext.PAGE_SCOPE:
      attrs.remove(name);
      break;
    default:
      throw new IllegalArgumentException("Illegal scope argument: " + scope);
    }
  }

  public int getAttributesScope(String name) {
    throw new UnsupportedOperationException();
  }

  public Object findAttribute(String name) {
    Object o = attrs.get(name);
    if (o != null)
      return o;
    
    o = getRequest().getAttribute(name);
    if (o != null)
      return o;
    
    HttpSession session = getSession();
    if (session != null) {
      o = session.getAttribute(name);
      if (o != null)
        return o;
    }
    
    return getServletContext().getAttribute(name);
  }
  
  public Enumeration getAttributeNamesInScope(int scope) {
    throw new UnsupportedOperationException();
  }

  public void removeAttribute(String name) {
    attrs.remove(name);
  }

  public JspWriter getOut() {
    return out;
  }

  public HttpSession getSession() {
    return session;
  }
    
  public Servlet getServlet() {
    throw new UnsupportedOperationException();
  }
    
  public ServletConfig getServletConfig() {
    return config;
  }
    
  public ServletContext getServletContext() {
    return config.getServletContext();
  }
    
  public ServletRequest getRequest() {
    return request;
  }
    
  public ServletResponse getResponse() {
    throw new UnsupportedOperationException();
  }
    
  public Exception getException() {
    throw new UnsupportedOperationException();
  }
    
  public Object getPage() {
    throw new UnsupportedOperationException();
  }

  public void include(String relativeUrlPath)
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  public void forward(String relativeUrlPath)
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  public BodyContent pushBody() {
    JspWriter previous = out;
    writerStack.push(out);
    out = new FakeBodyContent(previous);
    return (BodyContent) out;
  }

  public JspWriter popBody() {
    out = (JspWriter) writerStack.pop();
    return out;
  }

  public void handlePageException(Exception e)
    throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }
  
  public void handlePageException(Throwable t)
    throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  // jsp 2.0

  public void include ( String relativeUrlPath, boolean flush ) 
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  public VariableResolver getVariableResolver() {
    throw new UnsupportedOperationException();
  }

  public ExpressionEvaluator getExpressionEvaluator() {
    throw new UnsupportedOperationException();
  }

}
