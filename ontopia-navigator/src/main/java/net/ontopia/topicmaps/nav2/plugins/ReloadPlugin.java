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

import javax.servlet.http.HttpServletRequest;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Plugin implementation for a Reload Link resp. Button.
 */
public class ReloadPlugin extends DefaultPlugin {
  
  @Override
  public String generateHTML(ContextTag context) {
    if (context == null) {
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    }

    HttpServletRequest request = (HttpServletRequest)
      context.getPageContext().getRequest();
    
    String tm = context.getTopicMapId();
    // Deactivate plugin if there is no current topic map
    if (tm == null) {
      return "Reload";
    }
    
    // WARN: Note that only a single object id is supported.
    String[] oids = context.getObjectIDs();
    String objectid = (oids != null && oids.length != 0 ? oids[0] : null);
    
    String redirect_url = request.getRequestURI();
    
    StringBuilder sb = new StringBuilder();

    String style = getParameter("style");
    if (style != null && style.equals("button")) {
      // NOTE: No URL encoding
      if (request.getQueryString() != null) {
        redirect_url  += "?" + request.getQueryString();
      }
      String redirect_html = "<input type='hidden' name='redirect' value='" + redirect_url + "'>";
      sb.append("<form method='post' action='")
        .append(request.getContextPath())
        .append("/plugins/manage/manage.jsp' style='display: inline'");
      if (description != null) {
        sb.append(" title=\"").append(description).append('\"');
      }
      sb.append('>')
        .append("<input type='submit' value='Reload' style='font-size:10px'>")
        .append("<input type='hidden' name='action' value='reload'>")
        .append("<input type='hidden' name='id' value='").append(tm).append("'>");
      if (objectid != null) {
        sb.append("<input type='hidden' name='objectid' value='").append(objectid).append("'>");
      }
      sb.append(redirect_html);
      sb.append("</form>");
    } else {
      String title = getParameter("title");
      if (title == null) {
        title = "Reload";
      }

      // NOTE: URL encoding
      if (request.getQueryString() != null) {
        redirect_url  += "?" + java.net.URLEncoder.encode(request.getQueryString());
      }
      sb.append("<a href='")
        .append(request.getContextPath())
        .append("/plugins/manage/manage.jsp?action=reload&id=").append(tm)
        .append("&redirect=").append(redirect_url);
      if (objectid != null) {
        sb.append("&objectid=").append(objectid);
      }
      sb.append('\'');
      if (description != null) {
        sb.append(" title=\"").append(description).append('\"');
      }
      sb.append(">").append(title).append("</a>");
    }
    
    return sb.toString();
  }

}
