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

import com.hp.hpl.jena.rdfxml.xmlinput.ARP;
import com.hp.hpl.jena.rdfxml.xmlinput.StatementHandler;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Various utilities for working with RDF.
 */
public class RDFUtils {

  /**
   * Parses the RDF/XML at the given URL into the given StatementHandler.
   */
  public static void parseRDFXML(String url, StatementHandler handler)
    throws IOException {
    ARP parser = new ARP();
    parser.getHandlers().setStatementHandler(handler);

    URLConnection conn = new URL(url).openConnection();
    String encoding = conn.getContentEncoding();
    InputStream in = null;
    try {
      in = conn.getInputStream();
      if (encoding == null)
        parser.load(in, url);
      else
        parser.load(new InputStreamReader(in, encoding), url);
      in.close();
    } catch (org.xml.sax.SAXException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (in != null)
        in.close();
    }
  }
}