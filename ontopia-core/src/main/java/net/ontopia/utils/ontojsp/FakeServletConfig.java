// $Id: FakeServletConfig.java,v 1.5 2005/09/08 10:00:53 ian Exp $

package net.ontopia.utils.ontojsp;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class FakeServletConfig implements ServletConfig {
  protected ServletContext context;
  protected Hashtable params;
  
  public FakeServletConfig(ServletContext context) {
    this(context, new Hashtable());
  }

  public FakeServletConfig(ServletContext context, Hashtable params) {
    this.context = context;
    this.params = params;
  }
  
  public ServletContext getServletContext() {
    return context;
  }

  public String getInitParameter(String name) {
    return (String)params.get(name);
  }

  public Enumeration getInitParameterNames() {
    return params.keys();
  }

  public String getServletName() {
    // TODO
    return null;
  }
}




