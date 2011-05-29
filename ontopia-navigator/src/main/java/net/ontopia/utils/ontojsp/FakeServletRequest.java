
package net.ontopia.utils.ontojsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Collections;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.utils.NullObject;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * Fake the ServletRequest, needed for execution of a servlet/JSP.
 */
public class FakeServletRequest implements HttpServletRequest {

  private Map params;
  private Map attrs;
  private Map headers;
  private String context_path;
  private String user;
  private FakeHttpSession session;
  private FakeServletContext context;

  public FakeServletRequest() {
    this(new HashMap(), new HashMap());
  }
  
  public FakeServletRequest(Map params) {
    this(params, new HashMap());
  }
      
  public FakeServletRequest(Map params, Map attrs) {
    this.params = params;
    this.attrs = attrs;
    this.headers = new HashMap();
  }
  
  public Object getAttribute(String name) {
    Object result = attrs.get(name);
    if (result == NullObject.INSTANCE) 
      return null;
    return result;
  }

  public Enumeration getAttributeNames() {
    return Collections.enumeration(attrs.keySet());
  }

  public void setAttribute(String name, Object value) {
    if (value == null) 
      attrs.put(name, NullObject.INSTANCE);
    else 
      attrs.put(name, value);
  }

  public void removeAttribute(String name) {
    attrs.remove(name);
  }
    
  public String getCharacterEncoding() {
    throw new UnsupportedOperationException();
  }

  public void setCharacterEncoding(String enc) {
    // noop
  }

  public int getContentLength() {
    throw new UnsupportedOperationException();
  }

  public String getContentType() {
    throw new UnsupportedOperationException();
  }

  public Cookie[] getCookies() {
    throw new UnsupportedOperationException();
  }
    
  public String getHeader(String name) {
    return (String) headers.get(name);
  }

  public Enumeration getHeaders(String name) {
    throw new UnsupportedOperationException();
  }

  public Enumeration getHeaderNames() {
    return Collections.enumeration(headers.keySet());
  }

  public ServletInputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * In the case of a single value parameter just return the string,
   * otherwise (in a multiple value case) return the first entry of
   * the string array.
   */
  public String getParameter(String name) {
    Object retVal = params.get(name);
    
    if (retVal == null)
      return null;
    
    if (retVal instanceof String[])
      retVal = ((String[])retVal)[0];
    
    if (retVal instanceof String)
      return (String)retVal;

    throw new OntopiaRuntimeException("The parameter name " + name
        + " should have returened a String or array of Strings, but gave a " 
        + retVal.getClass().getName());
  }

  /**
   * For a single value parameter return a string array with only one
   * element, otherwise return the whole original string array.
   */
  public String[] getParameterValues(String name) {
    // we *do* support in the fake environment to
    // have several values for the same request parameter
    Object val = params.get(name);
    
    if (val == null)
      return null;
    
    if (val instanceof String) {
      String[] sval = {(String) params.get(name)};
      return sval;
    }
    if (val instanceof String[])
      return (String[]) val;
    
    throw new OntopiaRuntimeException("The parameter name " + name
        + " should have returened a String or array of Strings, but gave a " 
        + val.getClass().getName());
  }

  public Enumeration getParameterNames() {
    return Collections.enumeration(params.keySet());
  }

  public Map getParameterMap() {
    return params;
  }
    
  public String getPathInfo() {
    throw new UnsupportedOperationException();
  }

  public String getPathTranslated() {
    throw new UnsupportedOperationException();
  }
    
  public String getProtocol() {
    throw new UnsupportedOperationException();
  }

  public String getQueryString() {
    throw new UnsupportedOperationException();
  }

  public String getRemoteUser() {
    return user;
  }

  public void setRemoteUser(String user) {
    this.user = user;
  }

  public String getScheme() {
    throw new UnsupportedOperationException();
  }

  public String getServerName() {
    throw new UnsupportedOperationException();
  }

  public int getServerPort() {
    throw new UnsupportedOperationException();
  }

  public BufferedReader getReader() throws IOException {
    throw new UnsupportedOperationException();
  }
    
  public String getRemoteAddr() {
    throw new UnsupportedOperationException();
  }

  public String getRemoteHost() {
    throw new UnsupportedOperationException();
  }

  public String getRequestURI() {
    throw new UnsupportedOperationException();
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    throw new UnsupportedOperationException();
  }

  public Locale getLocale() {
    throw new UnsupportedOperationException();
  }

  public Enumeration getLocales() {
    throw new UnsupportedOperationException();
  }

  public String getContextPath() {
    return context_path;
  }

  public void setContextPath(String context_path) {
    this.context_path = context_path;
  }

  public String getServletPath() {
    throw new UnsupportedOperationException();
  }

  public String getRealPath(String name) {
    throw new UnsupportedOperationException();
  }
    
  public boolean isSecure() {
    throw new UnsupportedOperationException();
  }

  public javax.servlet.http.HttpSession getSession(boolean create) {
    throw new UnsupportedOperationException();
  }
  
  public javax.servlet.http.HttpSession getSession() {
    if (session == null)
      session = new FakeHttpSession(context);
    return session;
  }
  
  public boolean isRequestedSessionIdValid() {
    throw new UnsupportedOperationException();
  }
  public boolean isRequestedSessionIdFromCookie() {
    throw new UnsupportedOperationException();
  }
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException();
  }
  public boolean isRequestedSessionIdFromUrl() {
    throw new UnsupportedOperationException();
  }

  public java.lang.String getAuthType() {
    throw new UnsupportedOperationException();
  }
  public long getDateHeader(String name) { 
    throw new UnsupportedOperationException();
  }
  public int getIntHeader(String name) {
    throw new UnsupportedOperationException();
  }
  public java.lang.String getMethod() {
    throw new UnsupportedOperationException();
  }
  public boolean isUserInRole(String user) {
    throw new UnsupportedOperationException();
  }
  public java.security.Principal getUserPrincipal() {
    throw new UnsupportedOperationException();
  }
  public java.lang.String getRequestedSessionId() {
    throw new UnsupportedOperationException();
  }
  public java.lang.StringBuffer getRequestURL() {
    throw new UnsupportedOperationException();
  }

  // servlets 2.4

  public int getLocalPort() {
    throw new UnsupportedOperationException();
  }

  public String getLocalAddr() {
    throw new UnsupportedOperationException();
  }

  public int getRemotePort() {
    throw new UnsupportedOperationException();
  }

  public String getLocalName() {
    throw new UnsupportedOperationException();
  }

  // --- Extra methods

  public void setServletContext(FakeServletContext context) {
    this.context = context;
  }
  
}
