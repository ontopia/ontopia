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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.xml.PrettyPrinter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * PUBLIC: A topic map writer that can write topic maps out as either
 * XTM 1.0, 2.0 or 2.1. The default is XTM 1.0.
 */
public class XTMTopicMapWriter implements TopicMapWriterIF {

  public static final String PROPERTY_ADD_IDS = "addIds";
  public static final String PROPERTY_EXPORT_SOURCE_LOCATORS = "exportSourceLocators";
  public static final String PROPERTY_FILTER = "filter";
  public static final String PROPERTY_VERSION = "version";

  protected ContentHandler out;
  
  // If writer is instantiated here we'll close it when we're done.
  protected Writer writer;
  
  protected Predicate filter;

  protected boolean export_srclocs = false;
  protected boolean add_ids = false;
  private XTMVersion xtm_version;

  private static XTMVersion DEFAULT_XTM_VERSION = XTMVersion.XTM_1_0;

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param file The file object to which the topic map is to be written.
   */
  public XTMTopicMapWriter(File file) throws IOException {
    this(file, "utf-8");
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.   
   * @param file The file object to which the topic map is to be written.
   * @param encoding The character encoding to write the topic map in.
   */
  public XTMTopicMapWriter(File file, String encoding) throws IOException {
    this.writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
    this.out = new PrettyPrinter(writer, encoding);
    this.xtm_version = DEFAULT_XTM_VERSION;
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the output stream
   * given in the arguments.   
   * @param stream The output stream to which the topic map is to be
   * written.
   * @param encoding The character encoding to write the topic map in.
   * @exception UnsupportedEncodingException Thrown when the character
   * encoding is not supported by the Java environment.
   */
  public XTMTopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    this(new OutputStreamWriter(stream, encoding), encoding);
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the Writer given in
   * the arguments; we do <em>not</em> recommend the use of this
   * method.
   *
   * @param writer The Writer to which the topic map is to be
   * written.
   * @param encoding The character encoding the Writer writes in.
   * Note that this <em>must</em> be set correctly, or the XML
   * document will not parse correctly.
   * @since 1.1
   */
  public XTMTopicMapWriter(Writer writer, String encoding) throws IOException {
    this.out = new PrettyPrinter(writer, encoding);
    this.xtm_version = DEFAULT_XTM_VERSION;
  }

  /**
   * PUBLIC: Sets a filter used to filter the topic map before export.
   * Only topics, associations, and other characteristics accepted by
   * the filter are included in the export.
   *
   * @since 3.0
   */
  public void setFilter(Predicate filter) {
    this.filter = filter;
  }

  /**
   * PUBLIC: Returns true if configured to add IDs to all elements.
   * @since 2.0
   */
  public boolean getAddIds() {
    return add_ids;
  }

  /**
   * PUBLIC: Tells the exporter whether or not to add IDs to all
   * elements. (Default: false.)
   * @since 2.0
   */
  public void setAddIds(boolean add_ids) {
    this.add_ids = add_ids;
  }

  /**
   * PUBLIC: Set XTM version to use on export.
   * @since 5.1.0
   */
  public void setVersion(final XTMVersion version) {
    xtm_version = version;
  }

  @Override
  public void write(TopicMapIF topicmap) throws IOException {
    try {
      if (xtm_version == XTMVersion.XTM_1_0) {
        XTMTopicMapExporter exporter = new XTMTopicMapExporter();
        if (filter != null) {
          exporter.setFilter(filter);
        }
        exporter.setExportSourceLocators(getExportSourceLocators());
        exporter.setAddIds(getAddIds());
        exporter.export(topicmap, out);
      } 
      else {
        XTM2TopicMapExporter exporter = new XTM2TopicMapExporter(XTMVersion.XTM_2_1 == xtm_version);
        if (filter != null) {
          exporter.setFilter(filter);
        }
        exporter.setExportItemIdentifiers(getExportSourceLocators());
        exporter.export(topicmap, out);
      }
      if (writer != null) {
        writer.close();
      }
    }
    catch (SAXException e) {
      if (e.getException() instanceof IOException) {
        throw (IOException) e.getException();
      }
      throw new IOException("XML writing problem: " + e.toString());
    }
  }

  /**
   * INTERNAL: Returns true if source locators should be exported.
   */
  public boolean getExportSourceLocators() {
    return export_srclocs;
  }

  /**
   * INTERNAL: Set the flag that says whether source locators should
   * be exported or not.
   */
  public void setExportSourceLocators(boolean export_srclocs) {
    this.export_srclocs = export_srclocs;
  }
    
  /**
   * Sets additional properties for the XTMTopicMapWriter. Accepted properties:
   * <ul><li>'addIds' (Boolean), corresponds to 
   * {@link #setAddIds(boolean)}</li>
   * <li>'exportSourceLocators' (Boolean), corresponds to 
   * {@link #setExportSourceLocators(boolean)}</li>
   * <li>'version' (XTMVersion), corresponds to 
   * {@link #setVersion(net.ontopia.topicmaps.xml.XTMVersion)}</li>
   * <li>'filter' (DeciderIF), corresponds to 
   * {@link #setFilter(net.ontopia.utils.DeciderIF)}</li>
   * </ul>
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    Object value = properties.get(PROPERTY_ADD_IDS);
    if ((value != null) && (value instanceof Boolean)) {
      setAddIds((Boolean) value);
    }
    value = properties.get(PROPERTY_EXPORT_SOURCE_LOCATORS);
    if ((value != null) && (value instanceof Boolean)) {
      setExportSourceLocators((Boolean) value);
    }
    value = properties.get(PROPERTY_VERSION);
    if ((value != null) && (value instanceof XTMVersion)) {
      setVersion((XTMVersion) value);
    }
    value = properties.get(PROPERTY_FILTER);
    if ((value != null) && (value instanceof Predicate)) {
      setFilter((Predicate) value);
    }
  }
}
