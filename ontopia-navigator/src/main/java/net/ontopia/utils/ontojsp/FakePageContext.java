/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import javax.el.ELContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * INTERNAL: Fake the PageContext, needed for execution of a JSP.
 */
public class FakePageContext extends PageContext {
  private Map<String, Object> attrs;
  private ServletRequest request;
  private JspWriter out;
  private final Stack<JspWriter> writerStack = new Stack<JspWriter>();
  private ServletContext context;
  private ServletConfig config;
  private HttpSession session;
  
  public FakePageContext(Writer out) {
    this(out, new HashMap<String, Object>());
  }

  public FakePageContext(Writer out, Map<String, Object> attrs) {
    this(out, attrs, new HashMap<String, String[]>(), ".");
  }
  
  public FakePageContext(Writer out, Map<String, Object> attrs, Map<String, String[]> params, String path) {
    this.attrs = attrs;
    this.out = new DefaultJspWriter(out);

    // set up default environment
    request = new FakeServletRequest(params, attrs);
    context = new FakeServletContext(path);
    config = new FakeServletConfig(context);
    session = new FakeHttpSession(context);
  }

  // -- internal mutators

  public void setAttributes(Map<String, Object> attrs) {
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
  
  @Override
  public void initialize(Servlet servlet, ServletRequest request,
                         ServletResponse response, String errorPageURL,
                         boolean needsSession, int bufferSize,
                         boolean autoFlush)
    throws IOException, IllegalStateException, IllegalArgumentException {
    throw new UnsupportedOperationException();
    // TODO
  }

  @Override
  public void release() {
    // TODO
  }

  @Override
  public Object getAttribute(String name) {
    return attrs.get(name);
  }

  @Override
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

  @Override
  public void setAttribute(String name, Object value) {
    // JSP spec doesn't allow nulls
    Objects.requireNonNull(value, "Null value not allowed");
    attrs.put(name, value);
  }

  @Override
  public void setAttribute(String name, Object value, int scope) {
    // JSP spec doesn't allow nulls
    Objects.requireNonNull(value, "Null value not allowed");

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

  @Override
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

  @Override
  public int getAttributesScope(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object findAttribute(String name) {
    Object o = attrs.get(name);
    if (o != null) {
      return o;
    }
    
    o = getRequest().getAttribute(name);
    if (o != null) {
      return o;
    }
    
    HttpSession session = getSession();
    if (session != null) {
      o = session.getAttribute(name);
      if (o != null) {
        return o;
      }
    }
    
    return getServletContext().getAttribute(name);
  }
  
  @Override
  public Enumeration<String> getAttributeNamesInScope(int scope) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeAttribute(String name) {
    attrs.remove(name);
  }

  @Override
  public JspWriter getOut() {
    return out;
  }

  @Override
  public HttpSession getSession() {
    return session;
  }
    
  public Servlet getServlet() {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public ServletConfig getServletConfig() {
    return config;
  }
    
  @Override
  public ServletContext getServletContext() {
    return config.getServletContext();
  }
    
  @Override
  public ServletRequest getRequest() {
    return request;
  }
    
  @Override
  public ServletResponse getResponse() {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public Exception getException() {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public Object getPage() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void include(String relativeUrlPath)
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forward(String relativeUrlPath)
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public BodyContent pushBody() {
    JspWriter previous = out;
    writerStack.push(out);
    out = new FakeBodyContent(previous);
    return (BodyContent) out;
  }

  @Override
  public JspWriter popBody() {
    out = writerStack.pop();
    return out;
  }

  @Override
  public void handlePageException(Exception e)
    throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public void handlePageException(Throwable t)
    throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  // jsp 2.0

  @Override
  public void include ( String relativeUrlPath, boolean flush ) 
    throws ServletException, IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public VariableResolver getVariableResolver() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionEvaluator getExpressionEvaluator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ELContext getELContext() {
    throw new UnsupportedOperationException();
  }
}
