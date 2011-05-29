
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
import net.ontopia.utils.StringUtils;
import net.ontopia.utils.StreamUtils;
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

  public ServletContext getContext(String path) {
    throw new UnsupportedOperationException();
  }

  public Object getAttribute(String name) {
    Object result = attrs.get(name);
    if (result == NullObject.INSTANCE) 
      return null;
    else
      return result;
  }

  public Enumeration getAttributeNames() {
    return attrs.keys();
  }

  public void removeAttribute(String name) {
    attrs.remove(name);
  } 

  public void setAttribute(String name, Object value) {
    if (value == null) 
      attrs.put(name, NullObject.INSTANCE);
    else 
      attrs.put(name, value);
  }
    
  public int getMajorVersion() {
    return majorVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public String getMimeType(String filename) {
    // FIXME: Make it possible to set the mime-type.
    return "text/plain";
  }

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

  public URL getResource(String path) throws MalformedURLException {
    File respath = new File(rootpath, path);
    return URIUtils.toURL(respath);
  }

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

  public RequestDispatcher getRequestDispatcher(String path) {
    throw new UnsupportedOperationException();
  }

  public RequestDispatcher getNamedDispatcher(String name) {
    throw new UnsupportedOperationException();
  }

  public String getServerInfo() {
    return "FakeServer/ontopia";
  }
    
  public String getInitParameter(String name) {
    return (String)initParams.get(name);
  }
    
  public Enumeration getInitParameterNames() {
    return initParams.elements();
  }
    
  public void log(String msg) {
    logger.info(msg);
  }

  public void log(String msg, Throwable t) {
    logger.info(msg, t);
  }
  
  public void log(Exception e, String msg) {
    logger.info(msg, e);
  }

  public Servlet getServlet(String name) throws ServletException {
    throw new UnsupportedOperationException();
  }

  public Enumeration getServlets() {
    Vector v = new Vector();
    return v.elements();
  }
    
  public Enumeration getServletNames() {
    Vector v = new Vector();
    return v.elements();
  }

  public String getServletContextName() {
    return "fakeContext";
  }

}
