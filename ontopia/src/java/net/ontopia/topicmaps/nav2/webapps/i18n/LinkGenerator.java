
// $Id: LinkGenerator.java,v 1.8 2008/06/13 08:36:27 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.i18n;

import javax.servlet.jsp.PageContext;

import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
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
    if (isTopic(type, "script") || isTopic(type, "abugida") ||
	isTopic(type, "featural") || isTopic(type, "alphabet") ||
	isTopic(type, "abjad") || isTopic(type, "syllabary") ||
         isTopic(type, "logosyllabary"))
      link = "script.jsp?id=%id%";

    else if (isTopic(type, "language"))
      link = "language.jsp?id=%id%";

    else if (isTopic(type, "country"))
      link = "country.jsp?id=%id%";

    else if (isTopic(type, "province"))
      link = "province.jsp?id=%id%";

    else if (isTopic(type, "script-family") ||
	     isTopic(type, "script-group"))
      link = "category.jsp?id=%id%";
    
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

    LocatorIF base = topic.getTopicMap().getStore().getBaseAddress();
    if (base == null)
      return false;
    
    LocatorIF sloc = base.resolveAbsolute("#" + id);
    TMObjectIF candidate = topic.getTopicMap().getObjectByItemIdentifier(sloc);
    return candidate != null && candidate.equals(topic);
  }
  
}
