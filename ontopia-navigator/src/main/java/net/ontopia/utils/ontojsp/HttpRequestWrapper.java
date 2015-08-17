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

import java.util.Enumeration;
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
  @Override
  public String getMethod() {
    return request.getMethod();
  }
  @Override
  public java.util.Enumeration getHeaders(String attr0) {
    return request.getHeaders(attr0);
  }
  @Override
  public String getAuthType() {
    return request.getAuthType();
  }
  @Override
  public javax.servlet.http.Cookie[] getCookies() {
    return request.getCookies();
  }
  @Override
  public long getDateHeader(String attr0) {
    return request.getDateHeader(attr0);
  }
  @Override
  public String getHeader(String attr0) {
    return request.getHeader(attr0);
  }
  @Override
  public java.util.Enumeration getHeaderNames() {
    return request.getHeaderNames();
  }
  @Override
  public int getIntHeader(String attr0) {
    return request.getIntHeader(attr0);
  }
  @Override
  public String getPathInfo() {
    return request.getPathInfo();
  }
  @Override
  public String getPathTranslated() {
    return request.getPathTranslated();
  }
  @Override
  public String getContextPath() {
    return request.getContextPath();
  }
  @Override
  public String getQueryString() {
    return request.getQueryString();
  }
  @Override
  public String getRemoteUser() {
    return request.getRemoteUser();
  }
  @Override
  public boolean isUserInRole(String attr0) {
    return request.isUserInRole(attr0);
  }
  @Override
  public java.security.Principal getUserPrincipal() {
    return request.getUserPrincipal();
  }
  @Override
  public String getRequestedSessionId() {
    return request.getRequestedSessionId();
  }
  @Override
  public String getRequestURI() {
    return request.getRequestURI();
  }
  @Override
  public StringBuffer getRequestURL() {
    return request.getRequestURL();
  }
  @Override
  public String getServletPath() {
    return request.getServletPath();
  }
  @Override
  public javax.servlet.http.HttpSession getSession(boolean attr0) {
    return request.getSession(attr0);
  }
  @Override
  public javax.servlet.http.HttpSession getSession() {
    return request.getSession();
  }
  @Override
  public boolean isRequestedSessionIdValid() {
    return request.isRequestedSessionIdValid();
  }
  @Override
  public boolean isRequestedSessionIdFromCookie() {
    return request.isRequestedSessionIdFromCookie();
  }
  @Override
  public boolean isRequestedSessionIdFromURL() {
    return request.isRequestedSessionIdFromURL();
  }
  @Override
  public boolean isRequestedSessionIdFromUrl() {
    return request.isRequestedSessionIdFromUrl();
  }
  @Override
  public String getScheme() {
    return request.getScheme();
  }
  @Override
  public String getProtocol() {
    return request.getProtocol();
  }
  @Override
  public javax.servlet.ServletInputStream getInputStream() 
    throws java.io.IOException {
    return request.getInputStream();
  }
  @Override
  public int getContentLength() {
    return request.getContentLength();
  }
  @Override
  public String getContentType() {
    return request.getContentType();
  }
  @Override
  public Object getAttribute(String attr0) {
    return request.getAttribute(attr0);
  }
  @Override
  public java.util.Enumeration getAttributeNames() {
    return request.getAttributeNames();
  }
  @Override
  public String getCharacterEncoding() {
    return request.getCharacterEncoding();
  }
  @Override
  public void setCharacterEncoding(String attr0) 
    throws java.io.UnsupportedEncodingException {
    request.setCharacterEncoding(attr0);
  }
  @Override
  public String getParameter(String attr0) {
    return request.getParameter(attr0);
  }
  @Override
  public java.util.Enumeration getParameterNames() {
    return request.getParameterNames();
  }
  @Override
  public String[] getParameterValues(String attr0) {
    return request.getParameterValues(attr0);
  }
  @Override
  public java.util.Map getParameterMap() {
    return request.getParameterMap();
  }
  @Override
  public String getServerName() {
    return request.getServerName();
  }
  @Override
  public int getServerPort() {
    return request.getServerPort();
  }
  @Override
  public java.io.BufferedReader getReader() 
    throws java.io.IOException {
    return request.getReader();
  }
  @Override
  public String getRemoteAddr() {
    return request.getRemoteAddr();
  }
  @Override
  public String getRemoteHost() {
    return request.getRemoteHost();
  }
  @Override
  public void setAttribute(String attr0, Object attr1) {
    request.setAttribute(attr0, attr1);
  }
  @Override
  public void removeAttribute(String attr0) {
    request.removeAttribute(attr0);
  }
  @Override
  public java.util.Locale getLocale() {
    return request.getLocale();
  }
  @Override
  public Enumeration getLocales() {
    return request.getLocales();
  }
  @Override
  public boolean isSecure() {
    return request.isSecure();
  }
  @Override
  public javax.servlet.RequestDispatcher getRequestDispatcher(String attr0) {
    return request.getRequestDispatcher(attr0);
  }
  @Override
  public String getRealPath(String attr0) {
    return request.getRealPath(attr0);
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

}
