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

package net.ontopia.persistence.proxy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.StringUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.Slf4jSaxErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL: Class that is able to read named query definitions from an XML representation.
 */

public class QueryDeclarations {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(QueryDeclarations.class.getName());
  
  class QueriesHandler extends DefaultHandler {

    protected QueryDescriptor qdesc;
    protected List params;
    protected List selects;
    protected String indname;
    protected Map indics;
    
    protected Class getClassByName(String class_name) {
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Class.forName(class_name, true, classLoader);
      } catch (ClassNotFoundException e) {
        log.error("Cannot find class " + e.getMessage());
        throw new OntopiaRuntimeException(e);
      }
    }

    public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
      if (name.equals("query")) {
        // Get query name
        String query_name = atts.getValue("name");
        if (query_name == null) 
          throw new OntopiaRuntimeException("query.name must be specified: " + query_name);

        // Get query result type
        String type = atts.getValue("type");
        if (type == null) 
          throw new OntopiaRuntimeException("query.type must be specified: " + type);

        // Get query identity lookup
        boolean lookup_identities = PropertyUtils.isTrue(atts.getValue("lookup"), true);

        // Create query descriptor
        qdesc = new QueryDescriptor(query_name, type, lookup_identities);
        selects = new ArrayList();
        params = new ArrayList();

        // Get fetch size
        String fetchSize = atts.getValue("fetchSize");
        if (fetchSize != null) {
	  try {
	    qdesc.setFetchSize(Integer.parseInt(fetchSize));
	  } catch (NumberFormatException e) {
	    throw new SAXException(e);
	  }
	}

      }
      else if (name.equals("select")) {

        // Get class type
        String klass = atts.getValue("class");
        if (klass != null) {
          selects.add(new QueryDescriptor.SelectField(getClassByName(klass),
                                                      QueryDescriptor.SelectField.SELECT_CLASS));
        } else {
          // Get class indicator
          String indicator = atts.getValue("class-indicator");
          if (indicator != null) {
            selects.add(new QueryDescriptor.SelectField(getIndicator(indicator),
                                                        QueryDescriptor.SelectField.SELECT_INDICATOR));
          } else {
            throw new OntopiaRuntimeException("select.<valuetype> must be specified.");
          }
        }
      }
      else if (name.equals("param")) {
        // Add parameter class
        params.add(getClassByName(atts.getValue("class")));
      }
      else if (name.equals("statement")) {
        // Get statement platform       
        String platform = atts.getValue("platform");
        if (platform == null) 
          throw new OntopiaRuntimeException("statement.platform must be specified: " + platform);
        String[] platforms = StringUtils.split(platform, ",");
        
        // Get query statement
        String query = atts.getValue("query");
        if (query == null) 
          throw new OntopiaRuntimeException("statement.query must be specified: " + query);
        
        // Add query statement
        qdesc.addStatement(platforms, query);
      }
      else if (name.equals("class-indicator")) {
        // Get indicator name
        indname = atts.getValue("name");
        if (indname == null) 
          throw new OntopiaRuntimeException("class-indicator.name must be specified: " + indname);

        indics = new HashMap();
      }
      else if (name.equals("indicator")) {
        // Get indicator token
        String token = atts.getValue("token");
        if (token == null) 
          throw new OntopiaRuntimeException("indicator.token must be specified: " + token);
        
        // Get indicator class
        Class klass = getClassByName(atts.getValue("class"));

        // Put indicator on map
        indics.put(token, klass);
      }
      else if (name.equals("queries")) {
        // Ignore
      }
      else {
        log.warn("Unknown element: " + name);
      }
    }

    public void endElement (String uri, String name, String qName) throws SAXException {
      if (name.equals("query")) {
        // Set query selects
        qdesc.setSelects(selects);
        
        // Set query parameters
        qdesc.setParameters(params);

        // Register query with query declaration manager
        addQuery(qdesc);
        
        // Reset handler fields
        qdesc = null;
        selects = null;
        params = null;
      } 
      else if (name.equals("class-indicator")) {
        // Register class indicator with query declaration manager
        addIndicator(indname, indics);
        // Reset handler fields
        indname = null;
        indics = null;
      }
    }

  }

  protected Map queries = new HashMap();
  protected Map indicators = new HashMap();
  
  public QueryDeclarations(InputStream istream) {
    loadQueries(istream);
  }

  /**
   * INTERNAL: Gets the query descriptor by name.
   */
  public QueryDescriptor getQueryDescriptor(String name) {
    return (QueryDescriptor)queries.get(name);
  }
  
  /**
   * INTERNAL: Adds the query descriptor.
   */
  public void addQuery(QueryDescriptor qdesc) {
    String name = qdesc.getName();
    if (name == null) 
      throw new OntopiaRuntimeException("Cannot add query descriptor without a name: " + qdesc);
    
    queries.put(name, qdesc);
  }

  /**
   * INTERNAL: Looks up the class indicator map by name.
   */
  public Map getIndicator(String name) {
    if (!indicators.containsKey(name))
      throw new OntopiaRuntimeException("No indicator with the name: " + name);
    
    return (Map)indicators.get(name);
  }

  /**
   * INTERNAL: Adds the class indicator map by name. The indicator map
   * is keyed by strings and has Class object values.
   */
  public void addIndicator(String name, Map indicator) {
    indicators.put(name, indicator);
  }
  
  protected void loadQueries(InputSource isource) {

    // Read queries file.
    ContentHandler handler = new QueriesHandler();
    
    try {
      XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
      parser.setContentHandler(handler);
      parser.setErrorHandler(new Slf4jSaxErrorHandler(log));
      parser.parse(isource);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void loadQueries(InputStream stream) {
    loadQueries(new InputSource(stream));
  }
}


