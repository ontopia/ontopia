
// $Id: LinkGenerator.java,v 1.8 2008/06/13 08:36:27 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.customers;

import java.util.Collection;
import javax.servlet.jsp.PageContext;

import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;


/**
 * INTERNAL.
 */
public class LinkGenerator implements LinkGeneratorIF {
  
  public String generate(ContextTag contextTag, TMObjectIF tmobj,
			 String topicmapId, String template)
    throws NavigatorRuntimeException {

    TopicIF topic = (TopicIF) tmobj;
    TopicIF type = null;
    if (!topic.getTypes().isEmpty())
      type = (TopicIF) CollectionUtils.getFirstElement(topic.getTypes());
    
    String link = "index.jsp";
    TopicIF organisation = getTopic(topic.getTopicMap(), "organisation");
    TypeHierarchyUtils utils = new TypeHierarchyUtils();
    Collection orgtypes = utils.getSubclasses(organisation);
    if (orgtypes.contains(type))
      link = "organisation.jsp?id=%id%";

    else if (isTopic(type, "project"))
      link = "project.jsp?id=%id%";

    else if (isTopic(type, "person"))
      link = "contact.jsp?id=%id%";

    link = StringUtils.replace(link, "%id%", tmobj.getObjectId());
    return link;
  }


  
  public String generate(ContextTag contextTag, TopicMapReferenceIF tmRefObj,
			 String template) 
    throws NavigatorRuntimeException {
    
    throw new NavigatorRuntimeException("DON'T CALL THIS METHOD.");
    
  }


  // --- Internal methods

  private boolean isTopic(TopicIF topic, String id) {
    if (topic == null)
      return false;
    TopicIF candidate = getTopic(topic.getTopicMap(), id);
    return candidate != null && candidate.equals(topic);
  }

  private TopicIF getTopic(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    if (base == null)
      return null;
    
    LocatorIF sloc = base.resolveAbsolute("#" + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(sloc);
  }
  
}
