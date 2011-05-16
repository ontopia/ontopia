// $Id: ValidationPlugin.java,v 1.1 2002/12/20 11:06:23 grove Exp $

package net.ontopia.topicmaps.schema.impl.osl.nav.plugins;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.plugins.DefaultPlugin;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

public class ValidationPlugin extends DefaultPlugin {

  public String generateHTML(ContextTag context) {

    ServletContext ctxt = context.getPageContext().getServletContext();
    HttpServletRequest request =
      (HttpServletRequest)context.getPageContext().getRequest();

    String tm = context.getTopicMapId();

    // Does the schme exist?
    String path = ctxt.getRealPath("/WEB-INF/schemas/" + tm + ".osl");
    if (!new File(path).exists())
      return "<span title=\"No OSL schema found at: " + path + "\">No schema</span>";
    else 
      return super.generateHTML(context);
  }
  
}
