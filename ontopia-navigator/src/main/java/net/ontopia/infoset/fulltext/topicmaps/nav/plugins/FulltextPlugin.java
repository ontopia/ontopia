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

package net.ontopia.infoset.fulltext.topicmaps.nav.plugins;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.plugins.DefaultPlugin;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.OntopiaUnsupportedException;

public class FulltextPlugin extends DefaultPlugin {

  @Override
  public String generateHTML(ContextTag context) {
    if (context == null) {
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    }
    
    ServletContext ctxt = context.getPageContext().getServletContext();
    HttpServletRequest request =
      (HttpServletRequest)context.getPageContext().getRequest();
    
    String tm = context.getTopicMapId();

    // does the index exist?
    boolean exists = false;
    String path = ctxt.getRealPath("/WEB-INF/indexes/" + tm);
    TopicMapIF topicmap = context.getTopicMap();
    
    if (topicmap != null) {
      SearcherIF searcher = null;
      try {
        searcher = (SearcherIF) topicmap.getIndex(SearcherIF.class.getName());
        exists = true;
      } catch (OntopiaUnsupportedException e) {
        exists = false;
      } finally {
        if (searcher != null) {
          try {
            searcher.close();
          } catch (IOException e) {
            //ignore
          }
        }
      }
    }
    if (!exists) {
      // resource is not available, so display ft-admin instead
      PluginIF admin_plugin = context.getNavigatorConfiguration().getPlugin("fulltext-admin");
      if (admin_plugin == null || admin_plugin.getState() != PluginIF.ACTIVATED) {
        return "<span title=\"No index found at: " + path + "\">Not indexed</span>";
      } else {
        return "<span title=\"No index found at: " + path + "\"><a href='" +
          request.getContextPath() + "/" + admin_plugin.getURI() + "'>Not indexed</a></span>";
      }
    }

    // action URI is relative to context path (for example: '/omnigator')
    String action = request.getContextPath() + "/" + getURI();
    
    // create the form
    String query = getParameter("query");
    if (query == null) {
      query = "";
    }

    String query_size = getParameter("query-size");
    if (query_size == null) {
      query_size = "10";
    }

    String type = getParameter("type");
    if (type == null || type.equals("form")) {
      StringBuilder sb = new StringBuilder();
      sb.append("<form action='").append(action)
        .append("' method='get' style='display: inline'");
      if (description != null) {
        sb.append(" title=\"").append(description).append('\"');
      }
      sb.append('>')
        .append("<input type='hidden' value='").append(tm).append("' name='tm'>")
        .append("<input type='text' name='query' size='").append(query_size)
        .append("' value='").append(query).append("'>")
        .append("</form>");
      return sb.toString();
    } else {
      return super.generateHTML(context);
    }
  }
  
}
