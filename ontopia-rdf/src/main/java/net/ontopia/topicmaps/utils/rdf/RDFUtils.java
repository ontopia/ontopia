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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.jena.rdfxml.xmlinput.ARP;
import org.apache.jena.rdfxml.xmlinput.StatementHandler;

/**
 * INTERNAL: Various utilities for working with RDF.
 */
public class RDFUtils {

  /**
   * Parses the RDF/XML at the given URL into the given StatementHandler.
   */
  public static void parseRDFXML(URL url, StatementHandler handler)
    throws IOException {
    ARP parser = new ARP();
    parser.getHandlers().setStatementHandler(handler);

    URLConnection conn = url.openConnection();
    String encoding = conn.getContentEncoding();
    InputStream in = null;
    try {
      in = conn.getInputStream();
      if (encoding == null) {
        parser.load(in, url.toString());
      } else {
        parser.load(new InputStreamReader(in, encoding), url.toString());
      }
      in.close();
    } catch (org.xml.sax.SAXException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Parses the RDF/XML at the given InputStream into the given StatementHandler.
   */
  public static void parseRDFXML(InputStream in, StatementHandler handler)
    throws IOException {
    ARP parser = new ARP();
    parser.getHandlers().setStatementHandler(handler);

    try {
      parser.load(in);
    } catch (org.xml.sax.SAXException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }
}