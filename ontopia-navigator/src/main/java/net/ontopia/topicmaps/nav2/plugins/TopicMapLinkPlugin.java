
package net.ontopia.topicmaps.nav2.plugins;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

/**
 * INTERNAL: Plug-in that creates link to Omnigator topic map page.
 *
 * @since 3.4
 */
public class TopicMapLinkPlugin extends DefaultPlugin {
  
  public String generateHTML(ContextTag contextTag) {
    if (contextTag == null)
      throw new OntopiaRuntimeException("Plugin must have a parent tolog:context tag.");
    
    TopicMapIF tm = contextTag.getTopicMap();
    if (tm == null)
      return "<span>No topic map!</span>";

    // get omnigator model
    String model = FrameworkUtils.getUser(contextTag.getPageContext()).getModel();
        
    // get topic map id
    String tmid = contextTag.getTopicMapId();

    // get topic map title
    String tmtitle = "[No name]";
    TopicIF reifier = tm.getReifier();    
    if (reifier != null)
      tmtitle = TopicStringifiers.toString(reifier);
    else {
      TopicMapReferenceIF reference = tm.getStore().getReference();
      if (reference != null)
        tmtitle = reference.getTitle();
    }

    String tmids = getParameter("uri");
    
    return "<a href=\"" + uri + "/models/topicmap_" + model + ".jsp?tm=" + tmid + "\" title=\"Link to topic map page.\">" + tmtitle + "</a>";
  }

}
