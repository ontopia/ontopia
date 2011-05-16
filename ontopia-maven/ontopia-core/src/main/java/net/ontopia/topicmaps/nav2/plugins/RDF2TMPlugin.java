
// $Id: RDF2TMPlugin.java,v 1.3 2005/01/03 11:55:06 larsga Exp $

package net.ontopia.topicmaps.nav2.plugins;

import java.util.Collection;
import java.util.Iterator;
import java.net.URLEncoder;

import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;

import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Simple implementation of the RDF2TM plug-in.
 */
public class RDF2TMPlugin extends DefaultPlugin {
  
  public String generateHTML(ContextTag context) {
    if (context == null)
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    
    TopicMapIF tm = context.getTopicMap();
    if (tm == null)
      return "<span title=\"The RDF2TM plug-in can't edit the RDF-to-TM mappings, since you are not currently inside a topic map converted from RDF.\">No topic map!</a></span>";
    
    String tmid = context.getTopicMapId();
    TopicMapReferenceIF reference = tm.getStore().getReference();
    if (reference instanceof RDFTopicMapReference) {
      RDFTopicMapReference rdfref = (RDFTopicMapReference) reference;
      if (rdfref.getMappingFile() == null)
        return "<span title=\"This RDF file does not use the global mapping file, so the RDF2TM plug-in cannot configure it.\">No mapping</span>";
      else
        return "<a href=\"/omnigator/plugins/rdf2tm/configure.jsp?tm=" + tmid + "\" title=\"Modify the RDF-to-topic-map mapping.\">RDF2TM</a>";
    } else
      return null;
  }

}
