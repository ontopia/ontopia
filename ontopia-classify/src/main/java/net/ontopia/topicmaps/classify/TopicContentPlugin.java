/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import org.xml.sax.SAXException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;

/**
 * INTERNAL: Classifier plugin which produces content from the topic
 * itself.
 */
public class TopicContentPlugin implements ClassifyPluginIF {

  public boolean isClassifiable(TopicIF topic) {
    return true; // sure, we can do this for any topic
  }

  public ClassifiableContentIF getClassifiableContent(TopicIF topic) {
    return new TopicAsContent(topic);
  }

  // ----- INTERNAL CLASS

  public static class TopicAsContent implements ClassifiableContentIF {
    private TopicIF topic;

    public TopicAsContent(TopicIF topic) {
      this.topic = topic;
    }
    
    public String getIdentifier() {
      return topic.getObjectId();
    }

    public byte[] getContent() {
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrettyPrinter pp = new PrettyPrinter(out);
        XTMFragmentExporter exporter = new XTMFragmentExporter();
        exporter.exportAll(Collections.singleton(topic).iterator(), pp);
        pp.endDocument();
        return out.toByteArray();
      } catch (SAXException e) {
        throw new OntopiaRuntimeException(e);
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}
