/*
 * #!
 * Ontopia Omnigator webapplication
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * INTERNAL: Simple implementation of the RDF2TM plug-in.
 */
public class RDF2TMPlugin extends DefaultPlugin {
  
  @Override
  public String generateHTML(ContextTag context) {
    if (context == null) {
      throw new OntopiaRuntimeException("Plugin must have a parent logic:context tag.");
    }
    
    TopicMapIF tm = context.getTopicMap();
    if (tm == null) {
      return "<span title=\"The RDF2TM plug-in can't edit the RDF-to-TM mappings, since you are not currently inside a topic map converted from RDF.\">No topic map!</a></span>";
    }
    
    String tmid = context.getTopicMapId();
    TopicMapReferenceIF reference = tm.getStore().getReference();
    if (reference instanceof RDFTopicMapReference) {
      RDFTopicMapReference rdfref = (RDFTopicMapReference) reference;
      if (rdfref.getMappingFile() == null) {
        return "<span title=\"This RDF file does not use the global mapping file, so the RDF2TM plug-in cannot configure it.\">No mapping</span>";
      } else {
        return "<a href=\"/omnigator/plugins/rdf2tm/configure.jsp?tm=" + tmid + "\" title=\"Modify the RDF-to-topic-map mapping.\">RDF2TM</a>";
      }
    } else {
      return null;
    }
  }

}
