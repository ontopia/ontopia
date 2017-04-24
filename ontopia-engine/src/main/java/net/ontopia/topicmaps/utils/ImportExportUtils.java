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
import java.util.Set;
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
   * PUBLIC: Given a file reference to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapReaderIF getReader (File file) throws IOException {
    return getReader (file.toURL ().toExternalForm ());
  }

  /**
   * PUBLIC: Given the file name or URL of a topic map, returns a
   * topic map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   */
  public static TopicMapReaderIF getReader (String filename_or_url) {
    return getReader(URIUtils.toURL(filename_or_url));
  }

  /**
   * PUBLIC: Given a locator referring to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', '.tmx', and
   * '.ltm'.
   * 
   * @since 2.0
   */
  private static TopicMapReaderIF getReader (URL url) {
    String address = url.toString();
    try {
      if (address.startsWith ("x-ontopia:tm-rdbms:"))
        return new RDBMSTopicMapReader (getTopicMapId (address));
      else if (address.endsWith (".xtm"))
        return new XTMTopicMapReader (url);
      else if (address.endsWith (".ltm"))
        return new LTMTopicMapReader (url);
      else if (address.endsWith (".tmx"))
        return new TMXMLReader (url);
      else if (address.endsWith (".xml"))
        return new TMXMLReader(url); 
      else if (address.endsWith (".ctm"))
        return new CTMTopicMapReader(url);
      else {
        for (ImportExportServiceIF service : services) {
          if (service.canRead(url)) {
            return service.getReader(url);
          }
        }
        // fallback
        return new XTMTopicMapReader (url);
      }
    } catch (MalformedURLException mufe) {
      throw new OntopiaRuntimeException(mufe);
    }
  }

  /**
   * PUBLIC: Given the file for a topicmap, returns a topicmap
   * writer of the right class. Uses the file extension to determine
   * what writer to create.  Supports '.xtm' and '.tmx'. If the suffix
   * is unknown, the default writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (File tmfile) throws IOException {
    return getWriter(tmfile, null);
  }

  /**
   * PUBLIC: Given the file for a topicmap, returns a topicmap
   * writer of the right class. Uses the file extension to determine
   * what writer to create.  Supports '.xtm' and '.tmx'. If the suffix
   * is unknown, the default writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (File tmfile, String encoding) throws IOException {
    String name = tmfile.getName();
    if (name.endsWith(".ltm")) {
      return new LTMTopicMapWriter(tmfile, encoding);
    } else if (name.endsWith(".tmx")) {
      return new TMXMLWriter(tmfile, encoding);
    } else if (name.endsWith(".xtm1")) {
      return new XTMTopicMapWriter(tmfile, encoding);
    } else {
      for (ImportExportServiceIF service : services) {
        if (service.canWrite(tmfile.toURI().toURL())) {
          return service.getWriter(new FileOutputStream(tmfile));
        }
      }
      // fallback
      return new XTM2TopicMapWriter(tmfile);
    }
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
