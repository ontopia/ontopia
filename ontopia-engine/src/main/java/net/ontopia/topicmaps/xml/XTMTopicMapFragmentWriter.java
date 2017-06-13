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

package net.ontopia.topicmaps.xml;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Collections;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

import net.ontopia.xml.PrettyPrinter;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapFragmentWriterIF;

/**
 * PUBLIC: A fragment writer for XTM 1.0.
 * @since 5.1.3
 */
public class XTMTopicMapFragmentWriter implements TopicMapFragmentWriterIF {
  private Writer out;
  private ContentHandler dh;
  private XTMFragmentExporter exporter;
  
  public XTMTopicMapFragmentWriter(OutputStream out, String encoding)
    throws IOException {
    this.out = new OutputStreamWriter(out, encoding);
    this.dh = new PrettyPrinter(this.out, encoding);
    this.exporter = new XTMFragmentExporter();
  }

  public XTMTopicMapFragmentWriter(Writer out) throws IOException {
    this.out = out;
    this.dh = new PrettyPrinter(this.out, null);
    this.exporter = new XTMFragmentExporter();
  }
  
  /**
   * PUBLIC: Starts the fragment.
   */
  public void startTopicMap() throws IOException {
    try {
      exporter.startTopicMap(dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Exports all the topics returned by the iterator, and
   * wraps them with startTopicMap() and endTopicMap() calls.
   */
  public void exportAll(Iterator<TopicIF> it) throws IOException {
    try {
      exporter.exportAll(it, dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Exports all the topics returned by the iterator.
   */
  public void exportTopics(Iterator<TopicIF> it) throws IOException {
    try {
      exporter.exportTopics(it, dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Exports the given topic.
   */
  public void exportTopic(TopicIF topic) throws IOException {
    exportTopics(Collections.singleton(topic).iterator());
  }
  
  /**
   * PUBLIC: Ends the fragment.
   */
  public void endTopicMap() throws IOException {
    try {
      exporter.endTopicMap(dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Controls whether or not internal references of the form
   * '#id' will be exported.
   * @since 5.2.0
   */
  public void setUseLocalIds(boolean use_local_ids) {
    exporter.setUseLocalIds(use_local_ids);
  }

  /**
   * PUBLIC: Whether or not internal references of the form '#id' will
   * be exported.
   * @since 5.2.0
   */
  public boolean getUseLocalIds() {
    return exporter.getUseLocalIds();
  }
}
