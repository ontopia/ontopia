
// $Id: HttpRequestWrapper.java,v 1.1 2003/08/14 19:31:42 larsga Exp $

package net.ontopia.utils.ontojsp;

import javax.servlet.http.HttpServletRequest;

/**
 * INTERNAL: Wrapper class for HttpServletRequest; needed because Tomcat
 * does not let us use the standard one. See ProcessServlet.
 */
public class HttpRequestWrapper implements HttpServletRequest {
  protected HttpServletRequest request;

  public HttpRequestWrapper(HttpServletRequest request) {
    this.request = request;
  }
  public String getMethod() {
    return request.getMethod();
  }
  public java.util.Enumeration getHeaders(String attr0) {
    return request.getHeaders(attr0);
  }
  public String getAuthType() {
    return request.getAuthType();
  }
  public javax.servlet.http.Cookie[] getCookies() {
    return request.getCookies();
  }
  public long getDateHeader(String attr0) {
    return request.getDateHeader(attr0);
  }
  public String getHeader(String attr0) {
    return request.getHeader(attr0);
  }
  public java.util.Enumeration getHeaderNames() {
    return request.getHeaderNames();
  }
  public int getIntHeader(String attr0) {
    return request.getIntHeader(attr0);
  }
  public String getPathInfo() {
    return request.getPathInfo();
  }
  public String getPathTranslated() {
    return request.getPathTranslated();
  }
  public String getContextPath() {
    return request.getContextPath();
  }
  public String getQueryString() {
    return request.getQueryString();
  }
  public String getRemoteUser() {
    return request.getRemoteUser();
  }
  public boolean isUserInRole(String attr0) {
    return request.isUserInRole(attr0);
  }
  public java.security.Principal getUserPrincipal() {
    return request.getUserPrincipal();
  }
  public String getRequestedSessionId() {
    return request.getRequestedSessionId();
  }
  public String getRequestURI() {
    return request.getRequestURI();
  }
  public StringBuffer getRequestURL() {
    return request.getRequestURL();
  }
  public String getServletPath() {
    return request.getServletPath();
  }
  public javax.servlet.http.HttpSession getSession(boolean attr0) {
    return request.getSession(attr0);
  }
  public javax.servlet.http.HttpSession getSession() {
    return request.getSession();
  }
  public boolean isRequestedSessionIdValid() {
    return request.isRequestedSessionIdValid();
  }
  public boolean isRequestedSessionIdFromCookie() {
    return request.isRequestedSessionIdFromCookie();
  }
  public boolean isRequestedSessionIdFromURL() {
    return request.isRequestedSessionIdFromURL();
  }
  public boolean isRequestedSessionIdFromUrl() {
    return request.isRequestedSessionIdFromUrl();
  }
  public String getScheme() {
    return request.getScheme();
  }
  public String getProtocol() {
    return request.getProtocol();
  }
  public javax.servlet.ServletInputStream getInputStream() 
    throws java.io.IOException {
    return request.getInputStream();
  }
  public int getContentLength() {
    return request.getContentLength();
  }
  public String getContentType() {
    return request.getContentType();
  }
  public Object getAttribute(String attr0) {
    return request.getAttribute(attr0);
  }
  public java.util.Enumeration getAttributeNames() {
    return request.getAttributeNames();
  }
  public String getCharacterEncoding() {
    return request.getCharacterEncoding();
  }
  public void setCharacterEncoding(String attr0) 
    throws java.io.UnsupportedEncodingException {
    request.setCharacterEncoding(attr0);
  }
  public String getParameter(String attr0) {
    return request.getParameter(attr0);
  }
  public java.util.Enumeration getParameterNames() {
    return request.getParameterNames();
  }
  public String[] getParameterValues(String attr0) {
    return request.getParameterValues(attr0);
  }
  public java.util.Map getParameterMap() {
    return request.getParameterMap();
  }
  public String getServerName() {
    return request.getServerName();
  }
  public int getServerPort() {
    return request.getServerPort();
  }
  public java.io.BufferedReader getReader() 
    throws java.io.IOException {
    return request.getReader();
  }
  public String getRemoteAddr() {
    return request.getRemoteAddr();
  }
  public String getRemoteHost() {
    return request.getRemoteHost();
  }
  public void setAttribute(String attr0, Object attr1) {
    request.setAttribute(attr0, attr1);
  }
  public void removeAttribute(String attr0) {
    request.removeAttribute(attr0);
  }
  public java.util.Locale getLocale() {
    return request.getLocale();
  }
  public java.util.Enumeration getLocales() {
    return request.getLocales();
  }
  public boolean isSecure() {
    return request.isSecure();
  }
  public javax.servlet.RequestDispatcher getRequestDispatcher(String attr0) {
    return request.getRequestDispatcher(attr0);
  }
  public String getRealPath(String attr0) {
    return request.getRealPath(attr0);
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

}
