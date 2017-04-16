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

package net.ontopia.topicmaps.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReader;
import net.ontopia.topicmaps.utils.ctm.CTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.xml.TMXMLReader;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.ServiceUtils;
import net.ontopia.utils.URIUtils;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Utilities for importing and exporting topic maps.
 * 
 * @since 1.2
 */
public class ImportExportUtils {

  private static Set<ImportExportServiceIF> services;
  
  static {
    loadServices();
  }

  private static void loadServices() {
    try {
      services = ServiceUtils.loadServices(ImportExportServiceIF.class);
    } catch (IOException ex) {
      LoggerFactory.getLogger(ImportExportUtils.class).error("Could not load import-export services", ex);
    }
  }
  
  /**
   * Returns the loaded ImportExportServiceIF services.
   * @return the loaded ImportExportServiceIF services.
   */
  @SuppressWarnings("unchecked")
  public static Set<ImportExportServiceIF> getServices() {
    return UnmodifiableSet.unmodifiableSet(services);
  }

  /**
   * PUBLIC: Given the topic map store properties file and file name
   * or URL of a topic map, returns a topic map reader of the right
   * class. Uses the file extension to determine what reader to
   * create. Supports the suffixes '.xtm' and '.ltm' and URI schemes
   * 'x-ontopia:tm-rdbms:'.
   * 
   * @since 1.2.4
   */
  public static TopicMapReaderIF getReader (String propfile,
      String filename_or_url) {

    if (filename_or_url.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (propfile, getTopicMapId (filename_or_url));
    // Otherwise fall back to the property-less getReader method
    return getReader (filename_or_url);
  }

  /**
   * PUBLIC: Given the topic map store properties and file name or URL
   * of a topic map, returns a topic map reader of the right
   * class. Uses the file extension to determine what reader to
   * create. Supports the suffixes '.xtm' and '.ltm' and URI schemes
   * 'x-ontopia:tm-rdbms:'.
   * 
   * @since 1.2.4
   */
  public static TopicMapReaderIF getReader (Map<String, String> properties,
      String filename_or_url) {

    if (filename_or_url.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (properties,
          getTopicMapId (filename_or_url));
    // Otherwise fall back to the property-less getReader method
    return getReader (filename_or_url);
  }

  /**
   * PUBLIC: Given a file reference to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapReaderIF getReader (java.io.File file)
      throws java.io.IOException {
    return getReader (file.toURL ().toExternalForm ());
  }

  /**
   * PUBLIC: Given the file name or URL of a topic map, returns a
   * topic map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   */
  public static TopicMapReaderIF getReader (String filename_or_url) {
    return getReader (URIUtils.getURI (filename_or_url));
  }

  /**
   * PUBLIC: Given a locator referring to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', '.tmx', and
   * '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapReaderIF getReader (LocatorIF url) {
    String address = url.getAddress ();
    URL u;
    try {
      u = new URL(address);
    } catch (MalformedURLException mufe) {
      throw new OntopiaRuntimeException(mufe); // should not be possible
    }

    if (address.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (getTopicMapId (address));
    else if (address.endsWith (".xtm"))
      return new XTMTopicMapReader (u, url);
    else if (address.endsWith (".ltm"))
      return new LTMTopicMapReader (u, url);
    else if (address.endsWith (".tmx"))
      return new TMXMLReader (u, url);
    else if (address.endsWith (".xml"))
      return new TMXMLReader(u, url); 
    else if (address.endsWith (".ctm"))
      return new CTMTopicMapReader(u, url);
    else {
      for (ImportExportServiceIF service : services) {
        if (service.canRead(u)) {
          return service.getReader(u);
        }
      }
      // fallback
      return new XTMTopicMapReader (u, url);
    }
  }

  /**
   * PUBLIC: Given the file name or URL of a topic map, returns a
   * topic map importer of the right class. Uses the file extension to
   * determine what importer to create. Supports '.xtm', and '.ltm'.
   */
  public static TopicMapImporterIF getImporter (String filename_or_url) {
    return getImporter (URIUtils.getURI (filename_or_url));
  }

  /**
   * PUBLIC: Given a locator referring to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', '.tmx', and
   * '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapImporterIF getImporter (LocatorIF url) {
    String address = url.getAddress ();
    URL u;
    try {
      u = new URL(address);
    } catch (MalformedURLException mufe) {
      throw new OntopiaRuntimeException(mufe); // should not be possible
    }

    if (address.endsWith (".xtm"))
      return new XTMTopicMapReader (u, url);
    else if (address.endsWith (".ltm"))
      return new LTMTopicMapReader (u, url);
    else if (address.endsWith (".tmx"))
      return new TMXMLReader (u, url);
    else if (address.endsWith (".xml"))
      return new TMXMLReader(u, url); 
    else if (address.endsWith (".ctm"))
      return new CTMTopicMapReader(u, url);
    else {
      for (ImportExportServiceIF service : services) {
        if (service.canRead(u)) {
          return service.getImporter(u);
        }
      }
      // fallback
      return new XTMTopicMapReader (u, url);
    }
  }

  /**
   * PUBLIC: Given the file name of a topicmap, returns a topicmap
   * writer of the right class. Uses the file extension to determine
   * what writer to create.  Supports '.xtm' and '.tmx'. If the suffix
   * is unknown, the default writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (String tmfile)
      throws java.io.IOException {
    if (tmfile.endsWith (".ltm"))
      return new LTMTopicMapWriter (new FileOutputStream (tmfile));
    else if (tmfile.endsWith (".tmx"))
      return new TMXMLWriter (tmfile);
    else if (tmfile.endsWith (".xtm1"))
      return new XTMTopicMapWriter (new File (tmfile));
    else {
      for (ImportExportServiceIF service : services) {
        if (service.canWrite(new File(tmfile).toURI().toURL())) {
          return service.getWriter(new FileOutputStream(tmfile));
        }
      }
      // fallback
      return new XTM2TopicMapWriter (new File (tmfile));
    }
  }

  /**
   * PUBLIC: Given the file name of a topicmap, returns a topicmap writer of the
   * right class. Uses the file extension to determine what writer to create.
   * Supports '.xtm' and '.tmx'. If the suffix is unknown, the default
   * writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (String tmfile, String encoding)
      throws java.io.IOException {
    if (encoding == null)
      return getWriter(tmfile);

    if (tmfile.endsWith(".tmx"))
      return new TMXMLWriter (tmfile, encoding);
    else if (tmfile.endsWith(".xtm1"))
      return new XTMTopicMapWriter(new File(tmfile), encoding);
    else
      return new XTM2TopicMapWriter(new File(tmfile), encoding);
  }

  /**
   * INTERNAL: Gets the numeric topic map id from an RDBMS URI or a
   * simple topic map id reference. Examples: x-ontopia:tm-rdbms:123,
   * x-ontopia:tm-rdbms:M123, 123 and M123.
   */
  public static long getTopicMapId (String address) {
    int offset = 0;
    if (address.startsWith("M"))
      offset = 1;
    else if (address.startsWith("x-ontopia:tm-rdbms:")) {
      // Syntax: x-ontopia:tm-rdbms:12345
      offset = "x-ontopia:tm-rdbms:".length ();
      
      // Ignore M suffix on topic map id
      if (address.charAt (offset) == 'M')
        offset = offset + 1;
    }
    
    try {
      return Long.parseLong (address.substring (offset));
    } catch (NumberFormatException e) {
      throw new OntopiaRuntimeException ("'" + address
          + " is not a valid rdbms topic map URI.");
    }
  }
}
