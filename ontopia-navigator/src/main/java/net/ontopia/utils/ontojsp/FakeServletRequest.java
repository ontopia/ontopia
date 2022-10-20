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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * Fake the ServletRequest, needed for execution of a servlet/JSP.
 */
public class FakeServletRequest implements HttpServletRequest {

  private Map<String, String[]> params;
  private Map<String, Object> attrs;
  private Map<String, String> headers;
  private String context_path;
  private String user;
  private FakeHttpSession session;
  private FakeServletContext context;

  public FakeServletRequest() {
    this(new HashMap<String, String[]>(), new HashMap<String, Object>());
  }
  
  public FakeServletRequest(Map<String, String[]> params) {
    this(params, new HashMap<String, Object>());
  }
      
  public FakeServletRequest(Map<String, String[]> params, Map<String, Object> attrs) {
    this.params = params;
    this.attrs = attrs;
    this.headers = new HashMap<String, String>();
  }
  
  @Override
  public Object getAttribute(String name) {
    return attrs.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(attrs.keySet());
  }

  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) { 
      attrs.remove(name);
    } else {
      attrs.put(name, value);
    }
  }

  @Override
  public void removeAttribute(String name) {
    attrs.remove(name);
  }
    
  @Override
  public String getCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setCharacterEncoding(String enc) {
    // noop
  }

  @Override
  public int getContentLength() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContentType() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Cookie[] getCookies() {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public String getHeader(String name) {
    return headers.get(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    return Collections.enumeration(headers.keySet());
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * In the case of a single value parameter just return the string,
   * otherwise (in a multiple value case) return the first entry of
   * the string array.
   */
  @Override
  public String getParameter(String name) {
    Object retVal = params.get(name);
    
    if (retVal == null) {
      return null;
    }
    
    if (retVal instanceof String[]) {
      retVal = ((String[])retVal)[0];
    }
    
    if (retVal instanceof String) {
      return (String)retVal;
    }

    throw new OntopiaRuntimeException("The parameter name " + name
        + " should have returened a String or array of Strings, but gave a " 
        + retVal.getClass().getName());
  }

  /**
   * For a single value parameter return a string array with only one
   * element, otherwise return the whole original string array.
   */
  @Override
  public String[] getParameterValues(String name) {
    // we *do* support in the fake environment to
    // have several values for the same request parameter
    return  params.get(name);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(params.keySet());
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return params;
  }
    
  @Override
  public String getPathInfo() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPathTranslated() {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public String getProtocol() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getQueryString() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRemoteUser() {
    return user;
  }

  public void setRemoteUser(String user) {
    this.user = user;
  }

  @Override
  public String getScheme() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getServerName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getServerPort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public String getRemoteAddr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRemoteHost() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRequestURI() {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Locale getLocale() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<Locale> getLocales() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContextPath() {
    return context_path;
  }

  public void setContextPath(String context_path) {
    this.context_path = context_path;
  }

  @Override
  public String getServletPath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRealPath(String name) {
    throw new UnsupportedOperationException();
  }
    
  @Override
  public boolean isSecure() {
    throw new UnsupportedOperationException();
  }

  @Override
  public javax.servlet.http.HttpSession getSession(boolean create) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public javax.servlet.http.HttpSession getSession() {
    if (session == null) {
      session = new FakeHttpSession(context);
    }
    return session;
  }
  
  @Override
  public boolean isRequestedSessionIdValid() {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean isRequestedSessionIdFromCookie() {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean isRequestedSessionIdFromUrl() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAuthType() {
    throw new UnsupportedOperationException();
  }
  @Override
  public long getDateHeader(String name) { 
    throw new UnsupportedOperationException();
  }
  @Override
  public int getIntHeader(String name) {
    throw new UnsupportedOperationException();
  }
  @Override
  public String getMethod() {
    throw new UnsupportedOperationException();
  }
  @Override
  public boolean isUserInRole(String user) {
    throw new UnsupportedOperationException();
  }
  @Override
  public java.security.Principal getUserPrincipal() {
    throw new UnsupportedOperationException();
  }
  @Override
  public String getRequestedSessionId() {
    throw new UnsupportedOperationException();
  }
  @Override
  public StringBuffer getRequestURL() {
    throw new UnsupportedOperationException();
  }

  // servlets 2.4

  @Override
  public int getLocalPort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalAddr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRemotePort() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getLocalName() {
    throw new UnsupportedOperationException();
  }

  // servlet 2.5, 3.0
  
  @Override
  public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void login(String username, String password) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void logout() throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Part getPart(String name) throws IOException, ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletContext getServletContext() {
    return context;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncStarted() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAsyncSupported() {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DispatcherType getDispatcherType() {
    throw new UnsupportedOperationException();
  }  

  // --- Extra methods

  public void setServletContext(FakeServletContext context) {
    this.context = context;
  }
  
  public static Map<String, String[]> transform(Hashtable<String, Object> paramsTable) {
    Map<String, String[]> result = new LinkedHashMap<String, String[]>();
    for (String key : paramsTable.keySet()) {
      Object v = paramsTable.get(key);
      if (v instanceof String) {
        result.put(key, new String[] { (String) v });
      }
      if (v instanceof String[]) {
        result.put(key, (String[]) v);
      }
    }
    return result;
  }
}
