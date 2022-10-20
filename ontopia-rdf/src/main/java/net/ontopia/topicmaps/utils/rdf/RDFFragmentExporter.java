/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.utils.rdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapFragmentWriterIF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * PUBLIC: An RDF fragment exporter which produces RDF/XML. It does so
 * by first building an in-memory Jena model of the fragment, then
 * serializing the entire thing in one go to the stream. The exporter
 * is restricted in the sense that all topics must come from the same
 * topic map.
 *
 * @since 5.1.3
 */
public class RDFFragmentExporter implements TopicMapFragmentWriterIF {
  private Model model;
  private RDFTopicMapWriter serializer;
  private boolean setup;
  private OutputStream out;
  private String encoding;
  
  public RDFFragmentExporter(OutputStream out, String encoding) {
    this.out = out;
    this.encoding = encoding;
    this.model = ModelFactory.createDefaultModel();
    this.serializer = new RDFTopicMapWriter(model);
  }
  
  /**
   * PUBLIC: Exports all the topics returned by the iterator, and
   * wraps them with startTopicMap() and endTopicMap() calls.
   */
  @Override
  public void exportAll(Iterator<TopicIF> it) throws IOException {
    startTopicMap();
    exportTopics(it);
    endTopicMap();
  }

  /**
   * PUBLIC: Starts the fragment.
   */
  @Override
  public void startTopicMap() {
    // nothing to do here
  }

  /**
   * PUBLIC: Exports all the topics returned by the iterator.
   */
  @Override
  public void exportTopics(Iterator<TopicIF> it) throws IOException {
    while (it.hasNext()) {
      TopicIF topic = it.next();
      if (!setup) {
        serializer.setup(topic.getTopicMap());
        setup = true;
      }
      
      serializer.write(topic);
      for (AssociationRoleIF role : topic.getRoles()) {
        serializer.write(role.getAssociation());
      }
    }
  }

  /**
   * PUBLIC: Exports the given topic.
   */
  @Override
  public void exportTopic(TopicIF topic) throws IOException {
    exportTopics(Collections.singleton(topic).iterator());
  }
  
  /**
   * PUBLIC: Ends the fragment.
   */
  @Override
  public void endTopicMap() throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
    model.write(writer);
    writer.flush();
  }
}