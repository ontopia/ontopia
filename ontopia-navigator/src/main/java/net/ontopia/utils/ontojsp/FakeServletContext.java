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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import net.ontopia.utils.NullObject;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.URIUtils;
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
  private Hashtable attrs;
  private Hashtable initParams;

  private int majorVersion;

  private int minorVersion;

  public FakeServletContext() {
    this("/path/to/context");
  }
  
  public FakeServletContext(String rootpath) {
    this(rootpath, new Hashtable());
  }
  
  public FakeServletContext(String rootpath, Hashtable attrs) {
    this(rootpath, attrs, new Hashtable());
  }

  public FakeServletContext(String rootpath, Hashtable attrs, Hashtable initParams) {
    this.rootpath = rootpath;
    if (!this.rootpath.endsWith("/")) {
      this.rootpath += '/';
    }
    this.attrs = attrs;
    this.initParams = initParams;
    setVersion(2,3);
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
    Object result = attrs.get(name);
    if (result == NullObject.INSTANCE) 
      return null;
    else
      return result;
  }

  @Override
  public Enumeration getAttributeNames() {
    return attrs.keys();
  }

  @Override
  public void removeAttribute(String name) {
    attrs.remove(name);
  } 

  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) 
      attrs.put(name, NullObject.INSTANCE);
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
    for (int ix = 0; ix < components.length; ix++) {
      logger.debug(" - comp: " + components[ix]);
      logger.debug(" - current " + current);
      if (components[ix].equals("") || components[ix].equals("."))
        continue;
      
      else if (components[ix].equals(".."))
        current = current.getParentFile();

      else
        current = new File(current, components[ix]);
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
  public Set getResourcePaths(String path) {
    Set paths = new HashSet();
    File directory = new File(rootpath, path);
    logger.debug("getResourcePaths in dir: "+directory);
    String[] filenames = directory.list();
    // logger.debug("--> files: "+filenames);
    for (int i=0; i < filenames.length; i++) {
      paths.add(path+"/"+filenames[i]);
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
    return (String)initParams.get(name);
  }
    
  @Override
  public Enumeration getInitParameterNames() {
    return initParams.elements();
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
  public Enumeration getServlets() {
    Vector v = new Vector();
    return v.elements();
  }
    
  @Override
  public Enumeration getServletNames() {
    Vector v = new Vector();
    return v.elements();
  }

  @Override
  public String getServletContextName() {
    return "fakeContext";
  }

}
