
// $Id: VizPlugin.java,v 1.2 2005/05/19 12:47:00 larsga Exp $

package net.ontopia.topicmaps.nav2.plugins;

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/** 
 * INTERNAL: Simple extension of the DefaultPlugin used to block
 * vizigation of certain topic maps that don't vizigate well.
 */
public class VizPlugin extends DefaultPlugin {
  private Set blocked;
  private String message;
  
  public void init() {
    message = getParameter("message");
    if (message == null)
      message = "Vizigation is disabled for this topic map";
    
    
    blocked = new CompactHashSet();
    String tmids = getParameter("blocked");
    if (tmids == null)
      return;

    String[] ids = StringUtils.split(tmids, ",");
    for (int ix = 0; ix < ids.length; ix++)
      blocked.add(ids[ix].trim());
  }
  
  public String generateHTML(ContextTag context) {
    if (context == null)
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    
    String tm = context.getTopicMapId();
    if (!blocked.contains(tm))
      return super.generateHTML(context);
      
    // generate HTML String
    return "<span title=\"" + message + "\">Vizigate</span>";
  }
}
