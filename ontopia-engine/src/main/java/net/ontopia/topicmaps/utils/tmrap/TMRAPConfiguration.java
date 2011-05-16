
// $Id: TMRAPConfiguration.java,v 1.2 2006/08/01 14:31:33 grove Exp $

package net.ontopia.topicmaps.utils.tmrap;

import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * INTERNAL: Used to hold the TMRAP configuration settings. Mainly
 * used in order to pass the configuration settings from the adapters
 * in to TMRAPImplementation.
 */
public class TMRAPConfiguration {
  private String servername;
  private String edituri;
  private String viewuri;

  public TMRAPConfiguration(Map config) throws ServletException {
    servername = (String)config.get("server_name");
    edituri = (String)config.get("edit_uri");
    viewuri = (String)config.get("view_uri");
    if (edituri == null && viewuri == null)
      throw new ServletException("One of the 'edit_uri' and 'view_uri' " +
                                 "parameters must be specified");
  }
  
  public TMRAPConfiguration(ServletConfig config) throws ServletException {
    servername = config.getInitParameter("server_name");
    edituri = config.getInitParameter("edit_uri");
    viewuri = config.getInitParameter("view_uri");
    if (edituri == null && viewuri == null)
      throw new ServletException("One of the 'edit_uri' and 'view_uri' " +
                                 "parameters must be specified");
  }

  public String getServerName() {
    return servername;
  }

  public String getEditURI() {
    return edituri;
  }

  public String getViewURI() {
    return viewuri;
  }
}
