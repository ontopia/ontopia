
package net.ontopia.topicmaps.nav2.plugins;

import java.util.Collection;
import java.util.Iterator;
import java.net.URLEncoder;

import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Plugin implementation for a Google search Link resp. Button.
 */
public class GooglePlugin extends DefaultPlugin {
  
  public String generateHTML(ContextTag context) {
    if (context == null)
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    
    // Deactivate plugin if there is no current topic(s)
    Collection topics = context.getObjects();
    if (topics == null || topics.isEmpty())
      return "Google it!";
    
    StringBuffer sb = new StringBuffer();

    // --- Title
    String title = getParameter("title");
    if (title == null)
      title = "Google it!";

    // Stringify the topic, get most appropiate name
    StringifierIF ts = TopicStringifiers.getDefaultStringifier();
    StringBuffer query = new StringBuffer();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      query.append(ts.toString(topic));
      // --- Parameter "types"
      if (getParameter("types") != null && getParameter("types").equals("yes")) {
        // List all the types as well
        Iterator iter = topic.getTypes().iterator();
        while (iter.hasNext()) {
          query.append(" ").append( ts.toString(iter.next()) );
        }
      }
    } // while it

    // --- Parameter "style"
    String style = getParameter("style");    
    if (style == null || style.equals("image")) {
      sb.append("<form method='get' action='http://www.google.com/search' ")
        .append("      target='_blank' style='display: inline'");
      if (description != null)
        sb.append(" title=\"").append(description).append('\"');
      sb.append('>')
        .append("<input type='hidden' name='safe' value='vss'>")
        .append("<input type='hidden' name='vss' value='1'>")
        .append("<input type='hidden' name='q' value='").append(query.toString()).append("'>")
        .append("<input type='image' src='images/Google_Logo_25gry.gif'")
        .append("       alt='Search with Google'>")
        .append("</form>");
    } else {
      sb.append("<a href='http://www.google.com/search?safe=vss&vss=1&q=")
        .append(URLEncoder.encode(query.toString())).append("'>")
        .append(title).append("</a>");
    }
    
    return sb.toString();
  }
  
}
