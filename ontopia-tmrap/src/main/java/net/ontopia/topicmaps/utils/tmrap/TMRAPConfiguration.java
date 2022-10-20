/*
 * #!
 * Ontopia TMRAP
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

  public TMRAPConfiguration(Map<String, String> config) throws ServletException {
    servername = config.get("server_name");
    edituri = config.get("edit_uri");
    viewuri = config.get("view_uri");
    if (edituri == null && viewuri == null) {
      throw new ServletException("One of the 'edit_uri' and 'view_uri' " +
                                 "parameters must be specified");
    }
  }
  
  public TMRAPConfiguration(ServletConfig config) throws ServletException {
    servername = config.getInitParameter("server_name");
    edituri = config.getInitParameter("edit_uri");
    viewuri = config.getInitParameter("view_uri");
    if (edituri == null && viewuri == null) {
      throw new ServletException("One of the 'edit_uri' and 'view_uri' " +
                                 "parameters must be specified");
    }
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
