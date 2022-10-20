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

package net.ontopia.topicmaps.query.spi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.SAXTracker;
import net.ontopia.xml.DefaultXMLReaderFactory;

/**
 * EXPERIMENTAL: HTTP searcher implementation.<p>
 */

public class HTTPSearcher extends AbstractSearcher {
  
  @Override
  public int getValueType() {
    return SearcherIF.STRING_VALUE; // TODO: should support other identities as well
  }

  @Override
  public SearchResultIF getResult(String query) {
    return new HTTPSearchResult(query);
  }

  private class HTTPSearchResult extends AbstractSearchResult {
    
    private Iterator hits;
    private Hit hit;
    
    HTTPSearchResult(String query) {
      // construct url
      String url = (String)parameters.get("url");
      if (url.lastIndexOf('?') >= 0) {
        url += "&query=" + URLEncoder.encode(query);
      } else {
        url += "?query=" + URLEncoder.encode(query);
      }

      // Create new parser object
      XMLReader parser;
      try {
        parser = DefaultXMLReaderFactory.createXMLReader();
        
      } catch (SAXException e) {
        throw new OntopiaRuntimeException("Problems occurred when creating SAX2 XMLReader", e);
      }
      SearchHandler handler = new SearchHandler();
      parser.setContentHandler(handler);
    
      try {
        // open connection
        URLConnection conn = new URL(url).openConnection();        
        // parse result
        parser.parse(new InputSource(conn.getInputStream()));
      } catch (SAXParseException e) {
        throw new OntopiaRuntimeException("XML parsing problem: " + e.toString() + " at: "+
                                          e.getSystemId() + ":" + e.getLineNumber() + ":" +
                                          e.getColumnNumber(), e);
      } catch (SAXException e) {
        if (e.getException() instanceof IOException) {
          throw new OntopiaRuntimeException(e.getException());
        }
        throw new OntopiaRuntimeException(e);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }

      List _hits = handler.getHits();
      this.hits = _hits.iterator();
    }
    
    @Override
    public boolean next() {
      if (hits.hasNext()) {
        this.hit = (Hit)hits.next();
        return true;
      } else {
        this.hit = null;
        return false;
      }
    }
    
    @Override
    public Object getValue() {
      return hit.getValue();
    }
    
    @Override
    public float getScore() {
      return hit.getScore();
    }
    
    @Override
    public void close() {
      // no-op
    }
  }

  private class SearchHandler extends SAXTracker {
    private List hits;
    public List getHits() {
      return hits;
    }
    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
      hits = new ArrayList();
    }
    @Override
    public void startElement(String nsuri, String lname, String qname,
                             Attributes attrs) throws SAXException {
      super.startElement(nsuri, lname, qname, attrs);
      String id = attrs.getValue("id");
      String relevance = attrs.getValue("relevance");
      if (id != null && relevance != null) {
        float score;
        try {
          score = Float.parseFloat(relevance);
        } catch (NumberFormatException e) {
          score = 0f;
        }
        hits.add(new Hit(id, score));
      }
    }

  }
}
