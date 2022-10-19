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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.URIUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Fake the ServletContext, needed for execution of servlets.
 */
public class FakeServletContext implements ServletContext {

  // initialization of log facility
  private static Logger logger = LoggerFactory
    .getLogger(FakeServletContext.class.getName());
  
  private String rootpath;
  private Map<String, Object> attrs;
  private Map<String, String> initParams;

  private int majorVersion;

  private int minorVersion;

  public FakeServletContext() {
    this("/path/to/context");
  }
  
  public FakeServletContext(String rootpath) {
    this(rootpath, new HashMap<String, Object>());
  }
  
  public FakeServletContext(String rootpath, Map<String, Object> attrs) {
    this(rootpath, attrs, new HashMap<String, String>());
  }

  public FakeServletContext(String rootpath, Map<String, Object> attrs, Map<String, String> initParams) {
    this.rootpath = rootpath;
    if (!this.rootpath.endsWith("/")) {
      this.rootpath += '/';
    }
    this.attrs = attrs;
    this.initParams = initParams;
    setVersion(3,0);
  }

  public void setVersion(int major, int minor) {
    majorVersion = major;
    minorVersion = minor;
  }

  @Override
  public ServletContext getContext(String path) {
    throw new UnsupportedOperationException();
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
  public void removeAttribute(String name) {
    attrs.remove(name);
  } 

  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) 
      attrs.remove(name);
    else 
      attrs.put(name, value);
  }
    
  @Override
  public int getMajorVersion() {
    return majorVersion;
  }

  @Override
  public int getMinorVersion() {
    return minorVersion;
  }

  @Override
  public String getMimeType(String filename) {
    // FIXME: Make it possible to set the mime-type.
    return "text/plain";
  }

  @Override
  public String getRealPath(String path) {
    if (rootpath.startsWith("file://")) {
      return getRealFilePath(path);
    }
    return rootpath + path;
  }
  
  public String getRealFilePath(String path) {
    File current = new File(rootpath);
    String[] components = StringUtils.split(path, "/");
    for (String component : components) {
      logger.debug(" - comp: " + component);
      logger.debug(" - current " + current);
      if ("".equals(component) || ".".equals(component)) {
      } else if ("..".equals(component)) {
        current = current.getParentFile();
      } else {
        current = new File(current, component);
      }
    }

    return current.toString();
  }

  @Override
  public InputStream getResourceAsStream(String path) {
    InputStream stream = null;
    String fullpath = rootpath + path;
    try {
      stream = StreamUtils.getInputStream(fullpath);
    } catch (IOException e) {
      logger.warn("Cannot locate file: " + fullpath);
    }
    return stream;
  }

  @Override
  public URL getResource(String path) throws MalformedURLException {
    File respath = new File(rootpath, path);
    return URIUtils.toURL(respath);
  }

  @Override
  public Set<String> getResourcePaths(String path) {
    Set<String> paths = new HashSet<String>();
    File directory = new File(rootpath, path);
    logger.debug("getResourcePaths in dir: "+directory);
    String[] filenames = directory.list();
    // logger.debug("--> files: "+filenames);
    for (String filename : filenames) {
      paths.add(path+"/" + filename);
    }
    return paths;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RequestDispatcher getNamedDispatcher(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getServerInfo() {
    return "FakeServer/ontopia";
  }
    
  @Override
  public String getInitParameter(String name) {
    return initParams.get(name);
  }
    
  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(initParams.keySet());
  }
    
  @Override
  public void log(String msg) {
    logger.info(msg);
  }

  @Override
  public void log(String msg, Throwable t) {
    logger.info(msg, t);
  }
  
  @Override
  public void log(Exception e, String msg) {
    logger.info(msg, e);
  }

  @Override
  public Servlet getServlet(String name) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Enumeration<Servlet> getServlets() {
    return Collections.enumeration(Collections.<Servlet>emptySet());
  }
    
  @Override
  public Enumeration<String> getServletNames() {
    return Collections.enumeration(Collections.<String>emptySet());
  }

  @Override
  public String getServletContextName() {
    return "fakeContext";
  }

  // servlet 2.5, 3.0
  
  @Override
  public String getContextPath() {
    return rootpath;
  }

  @Override
  public int getEffectiveMajorVersion() {
    return majorVersion;
  }

  @Override
  public int getEffectiveMinorVersion() {
    return minorVersion;
  }

  @Override
  public boolean setInitParameter(String name, String value) {
    initParams.put(name, value);
    return true;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String servletName, String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServletRegistration getServletRegistration(String servletName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    throw new UnsupportedOperationException();
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String filterName, String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public FilterRegistration getFilterRegistration(String filterName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    throw new UnsupportedOperationException();
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addListener(String className) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends EventListener> void addListener(T t) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addListener(Class<? extends EventListener> listenerClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
    throw new UnsupportedOperationException();
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClassLoader getClassLoader() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void declareRoles(String... roleNames) {
    throw new UnsupportedOperationException();
  }
}
