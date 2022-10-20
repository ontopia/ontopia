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

package net.ontopia.topicmaps.nav2.plugins;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/** 
 * INTERNAL: This is the plugin implementation that is used if no
 * other implementation is requested in the plugin.xml file.
 */
public class DefaultPlugin implements PluginIF {

  protected int state;
  
  protected String title;
  protected String description;
  protected String uri;
  protected String target;
  protected String id;
  protected String directory;
  protected Map params;
  protected List groups;

  public static final String RP_TOPICMAP_ID    = "tm";
  public static final String RP_TOPIC_ID       = "id";
  
  public DefaultPlugin() {
    params = new HashMap();
    groups = Collections.EMPTY_LIST;
    state = PluginIF.ACTIVATED;
  }

  // ----------------------------------------------------------
  // methods for implementing the PluginIF interface
  // ----------------------------------------------------------
  
  @Override
  public void init() {
    // no-op
  }

  @Override
  public String generateHTML(ContextTag context) {
    if (context == null) {
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    }
    
    String tm = context.getTopicMapId();
    String tmParam = context.getTmparam();
    if (tmParam == null) {
      tmParam = RP_TOPICMAP_ID;
    }
    String objidParam = context.getObjparam();
    if (objidParam == null) {
      objidParam = RP_TOPIC_ID;
    }
    String[] objids = context.getObjectIDs();
    if (objids == null) {
      objids = new String[] {context.getPageContext().getRequest().getParameter(objidParam)};
    }

    HttpServletRequest request = (HttpServletRequest)
      context.getPageContext().getRequest();
    // retrieve context path (for example: '/omnigator')
    String contextPath = request.getContextPath();
    
    // generate Link which is used by anchor element
    StringBuilder link = new StringBuilder(89);
    link.append(contextPath).append("/").append(uri)
      .append("?").append(tmParam).append("=").append(tm);
    if (objids != null) {
      for (int i=0; i < objids.length; i++) {
        link.append("&").append(objidParam).append("=").append(objids[i]);
      }
    }
    
    // append requested URI inclusive query string to link
    StringBuilder comingFrom = new StringBuilder(request.getRequestURI());
    if (request.getQueryString() != null) {
      comingFrom.append("?").append(request.getQueryString());
    }
    link.append("&redirect=").append(URLEncoder.encode(comingFrom.toString()));

    // generate HTML String
    StringBuilder html = new StringBuilder(50);
    html.append("<a href=\"").append(link.toString()).append('\"');
    if (description != null) {
      html.append(" title=\"").append(description).append('\"');
    }
    if (target != null) {
      html.append(" target=\"").append(target);
    }
    html.append("\">").append(title).append("</a>");
    
    return html.toString();
  }

  // ----------------------------------------------------------
  // Accessor methods
  // ----------------------------------------------------------
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public void resetGroups() {
    groups = new ArrayList();
  }
  
  @Override
  public List getGroups() {
    return groups;
  }

  @Override
  public void addGroup(String groupId) {
    groups.add(groupId);
  }
  
  @Override
  public void setGroups(List groups) {
    this.groups = groups;
  }
  
  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getURI() {
    return uri;
  }

  @Override
  public void setURI(String uri) {
    this.uri = uri;
  }

  @Override
  public String getTarget() {
    return target;
  }

  @Override
  public void setTarget(String target) {
    this.target = target;
  }

  @Override
  public int getState() {
    return state;
  }

  @Override
  public void setState(int state) {
    this.state = state;
  }
  
  @Override
  public String getParameter(String name) {
    return (String)params.get(name);
  }
  
  @Override
  public void setParameter(String name, String value) {
    params.put(name, value);
  }

  @Override
  public String getPluginDirectory() {
    return directory;
  }
  
  @Override
  public void setPluginDirectory(String path) {
    directory = path;
  }


  // ----------------------------------------------------------
  // extraordinary useful methods
  // ----------------------------------------------------------

  @Override
  public int hashCode() {
    StringBuilder sb = new StringBuilder(32);
    sb.append(id).append(title).append(uri);
    return sb.toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PluginIF)) {
      return false;
    }
    PluginIF compObj = (PluginIF) obj;
    return (compObj.getId().equals(id)
            && compObj.getTitle().equals(title)
            && compObj.getURI().equals(uri));
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // put out FQCN of plugin: this.getClass().getName()
    sb.append("[Plugin| " + getId())
      .append(" (" + getStateAsString() + ")");
    if (groups.size() > 0) {
      sb.append(" belongs to group(s): ");
      Iterator it = groups.iterator();
      while (it.hasNext()) {
        sb.append( (String) it.next() + " " );
      }
    } else {
      sb.append(" belongs to *NO* groups");
    }
    sb.append("]");
    return sb.toString();
  }

  protected String getStateAsString() {
    if (state == PluginIF.ACTIVATED) {
      return "activated";
    } else if (state == PluginIF.DEACTIVATED) {
      return "deactivated";
    } else if (state == PluginIF.ERROR) {
      return "error";
    } else {
      return "[undefined]";
    }
  }
}
