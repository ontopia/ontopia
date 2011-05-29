
package net.ontopia.infoset.fulltext.topicmaps.nav.plugins;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.plugins.DefaultPlugin;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.OntopiaRuntimeException;

public class FulltextPlugin extends DefaultPlugin {

  public String generateHTML(ContextTag context) {
    if (context == null)
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    
    ServletContext ctxt = context.getPageContext().getServletContext();
    HttpServletRequest request =
      (HttpServletRequest)context.getPageContext().getRequest();
    
    String tm = context.getTopicMapId();

    // does the index exist?
    boolean exists;
    String path = ctxt.getRealPath("/WEB-INF/indexes/" + tm);
    TopicMapIF topicmap = context.getTopicMap();
    if (topicmap != null && topicmap.getStore().getProperty("net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type") != null) {
      exists = true;
    } else {
      exists = new File(path).exists();
    }
    if (!exists) {
      // resource is not available, so display ft-admin instead
      PluginIF admin_plugin = context.getNavigatorConfiguration().getPlugin("fulltext-admin");
      if (admin_plugin == null || admin_plugin.getState() != PluginIF.ACTIVATED)
        return "<span title=\"No index found at: " + path + "\">Not indexed</span>";
      else
        return "<span title=\"No index found at: " + path + "\"><a href='" +
          request.getContextPath() + "/" + admin_plugin.getURI() + "'>Not indexed</a></span>";
    }

    // action URI is relative to context path (for example: '/omnigator')
    String action = request.getContextPath() + "/" + getURI();
    
    // create the form
    String query = getParameter("query");
    if (query == null) query = "";

    String query_size = getParameter("query-size");
    if (query_size == null) query_size = "10";

    String type = getParameter("type");
    if (type == null || type.equals("form")) {
      StringBuffer sb = new StringBuffer();
      sb.append("<form action='").append(action)
        .append("' method='get' style='display: inline'");
      if (description != null)
        sb.append(" title=\"").append(description).append('\"');
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
