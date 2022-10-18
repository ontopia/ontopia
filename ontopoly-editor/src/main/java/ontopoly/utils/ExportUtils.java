/*
 * #!
 * Ontopoly Editor
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
package ontopoly.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.function.Predicate;

import ontopoly.model.TopicMap;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.utils.deciders.TMExporterDecider;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapWriter;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.XTM21TopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMVersion;
import net.ontopia.utils.OntopiaRuntimeException;

public class ExportUtils {

  private ExportUtils() {
  }
  
  public static void export(TopicMap topicMap, String format,
                            Content content,
                            OutputStreamWriter out) {
    TopicMapIF tm = topicMap.getTopicMapIF();
    try {
      String charset = "utf-8";

      TopicMapWriterIF writer;
      Predicate filter = null;
      if (content == Content.INSTANCES_ONLY)
        filter = new SchemaFilter();
      else if (content == Content.SCHEMA_ONLY)
        filter = new SchemaOnlyFilter();

      // if filter == null it never gets used
      TMExporterDecider decider = null;
      if (filter != null)
        decider = new TMExporterDecider(filter);

      if (format.equalsIgnoreCase("xtm1")) { 
        XTMTopicMapWriter filterer = new XTMTopicMapWriter(out, charset);
        if (filter != null)
          filterer.setFilter(decider);
        filterer.setVersion(XTMVersion.XTM_1_0);
        writer = filterer;
      } else if (format.equalsIgnoreCase("xtm2")) { 
        XTM2TopicMapWriter filterer = new XTM2TopicMapWriter(out, charset);
        if (filter != null)
          filterer.setFilter(decider);
        filterer.setVersion(XTMVersion.XTM_2_0);
        writer = filterer;
      } else if (format.equalsIgnoreCase("xtm21")) { 
        XTM21TopicMapWriter filterer = new XTM21TopicMapWriter(out, charset);
        if (filter != null)
          filterer.setFilter(decider);
        filterer.setVersion(XTMVersion.XTM_2_1);
        writer = filterer;
      } else if (format.equalsIgnoreCase("rdf")) { 
        RDFTopicMapWriter filterer = new RDFTopicMapWriter(out);
        if (filter != null)
          filterer.setFilter(decider);
        writer = filterer;
      } else if (format.equalsIgnoreCase("ltm")) {
        LTMTopicMapWriter filterer = new LTMTopicMapWriter(out, charset);
        if (filter != null)
          filterer.setFilter(decider);
        writer = filterer;
      } else if (format.equalsIgnoreCase("tmxml")) {
        TMXMLWriter filterer = new TMXMLWriter(out);
        if (filter != null)
          filterer.setFilter(decider);
        writer = filterer;
      } else
        throw new OntopiaRuntimeException("Unsupported/unknown export format: " + format);
    
      writer.write(tm);

    } catch(IOException ioe) {
      throw new OntopiaRuntimeException(ioe);
    }
    
  }

  public enum Content {
    ENTIRE_TOPIC_MAP,
    INSTANCES_ONLY,
    SCHEMA_ONLY
  }
}
