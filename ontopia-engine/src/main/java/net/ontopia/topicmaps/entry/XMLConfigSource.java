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

package net.ontopia.topicmaps.entry;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.xml.Slf4jSaxErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * PUBLIC: Reads store configuration parameters from an XML
 * file. The config source is able to handle multiple sources at the
 * same time by using an instance of the {@link TopicMapRepositoryIF}
 * class.<p>
 *
 * The class understands XML documents using the following DTD:</p>
 *
 * <pre>
 * &lt;!ELEMENT repository (source+)              >
 * &lt;!ELEMENT source     (param*)               >
 * &lt;!ATTLIST source      id    ID    #IMPLIED  >
 * &lt;!ATTLIST source      class CDATA #REQUIRED >
 * &lt;!ELEMENT param       EMPTY                 >
 * &lt;!ATTLIST param       name  CDATA #REQUIRED
 *                          value CDATA #REQUIRED >
 * </pre>
 *
 * <b>Example:</b></p>
 *
 * <pre>
 * &lt;repository>
 *   &lt;!-- source that references all .xtm files in a directory -->
 *   &lt;source class="net.ontopia.topicmaps.xml.XTMPathTopicMapSource">
 *     &lt;param name="path" value="/ontopia/topicmaps"/>
 *     &lt;param name="suffix" value=".xtm"/>
 *   &lt;/source>
 *   &lt;!-- source that references a topic map in a relational database -->
 *   &lt;source class="net.ontopia.topicmaps.impl.rdbms.RDBMSSingleTopicMapSource">
 *     &lt;param name="topicMapId" value="5001"/>
 *     &lt;param name="title" value="My RDBMS Topic Map"/>
 *     &lt;param name="referenceId" value="mytm"/>
 *     &lt;param name="propertyFile" value="${CWD}/db.postgresql.props"/>
 *   &lt;/source>
 * &lt;/repository>
 * </pre>
 *
 * This example makes XMLConfigSource use a TopicMapRepositoryIF that
 * contains two other topic maps sources, namely instances of the two
 * specifed in the class attributes of the source elements. Note that
 * the classes must have empty constructors for them to be used with
 * this class.</p>
 *
 * The two sources would locate all XTM files with the .xtm extension
 * in the /ontopia/topicmaps directory in which the config file is
 * located.</p>
 *
 * The <code>param</code> element is used to set bean properties on
 * the source instance. This way the config source can be configured.</p>
 *
 * <p><b>Environment variables:</b></p>
 *
 * <p>XMLConfigSource is able to replace environment variables in the
 * param value attribute. The only environment variable available at
 * this time is ${CWD}, which contains the full path of the directory
 * in which the config file is located.</p>
 *
 * <p>NOTE: Topic map sources with supportsCreate set to true will get
 * ids assigned automatically. This is done so that the sources can be
 * referred to from the outside.</p>
 *
 */
public class XMLConfigSource {
  private static final String CWD = "CWD";

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(XMLConfigSource.class.getName());

  /**
   * INTERNAL: Don't call constructor directly. Instead used static
   * factory methods.
   */
  private XMLConfigSource() {
  }

  /**
   * PUBLIC: Get the topic map repository that is created by loading
   * the 'tm-sources.xml' configuration file from the classpath.<p>
   *
   * @since 3.0
   */
  public static TopicMapRepositoryIF getRepositoryFromClassPath() {
    return getRepositoryFromClassPath("tm-sources.xml");
  }

  /**
   * PUBLIC: Get the topic map repository that is created by loading
   * the named resource from the classpath.<p>
   *
   * @since 3.0
   */
  public static TopicMapRepositoryIF getRepositoryFromClassPath(String resourceName) {
    return getRepositoryFromClassPath(resourceName, null);    
  }

  /**
   * INTERNAL:
   */
  public static TopicMapRepositoryIF getRepositoryFromClassPath(Map<String, String> environ) {
    return getRepositoryFromClassPath("tm-sources.xml", environ);    
  }

  /**
   * INTERNAL:
   */
  public static TopicMapRepositoryIF getRepositoryFromClassPath(String resourceName, Map<String, String> environ) {

    // look up configuration via classpath
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    URL url = cl.getResource(resourceName);
    if (url == null) {
      throw new OntopiaRuntimeException("Could not find resource '" + resourceName + "' on CLASSPATH.");
    }
    
    // build configuration environment
    if (environ == null) {
      environ = new HashMap<String, String>(1);
    }
    if ("file".equals(url.getProtocol())) {
      String file = url.getFile();
      environ.put(CWD, file.substring(0, file.lastIndexOf('/')));
    } else {
      environ.put(CWD, ".");
    }

    // read configuration and create the repository instance
    try {
      return createRepository(readSources(new InputSource(url.openStream()),
                                          environ));
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }

  }

  /**
   * PUBLIC: Gets the topic map repository that is created by reading
   * the configuration file.<p>
   */
  public static TopicMapRepositoryIF getRepository(String config_file) {
    return createRepository(readSources(config_file));
  }

  /**
   * INTERNAL: Gets the topic map repository that is created by
   * reading the configuration file with the given environment.<p>
   */
  public static TopicMapRepositoryIF getRepository(String config_file, Map<String, String> environ) {
    return createRepository(readSources(config_file, environ));
  }

  /**
   * PUBLIC: Gets the topic map repository that is created by reading
   * the configuration file from the reader.<p>
   *
   * @since 3.0
   */
  public static TopicMapRepositoryIF getRepository(Reader config_file) {
    return createRepository(readSources(new InputSource(config_file), null));
  }

  /**
   * INTERNAL: Gets the topic map repository that is created by
   * reading the configuration file from the reader with the given
   * environment.<p>
   *
   * @since 3.0
   */
  public static TopicMapRepositoryIF getRepository(Reader config_file, Map<String, String> environ) {
    return createRepository(readSources(new InputSource(config_file), environ));
  }

  private static TopicMapSourceManager createRepository(Collection<TopicMapSourceIF> sources) {
    // assign default source ids and titles
    int counter = 1;
    Iterator<TopicMapSourceIF> iter = sources.iterator();
    while (iter.hasNext()) {
      TopicMapSourceIF source = iter.next();
      if (source.getId() == null && source.supportsCreate()) {
        String newId = source.getClass().getName() + "-" + (counter++);
        source.setId(newId);
        if (source.getTitle() == null) {
          source.setTitle(newId);
        }
      }
    }
    return new TopicMapSourceManager(sources);
  }
  
  /**
   * INTERNAL: Returns a collection containing the topic map sources
   * created by reading the configuration file.
   */
  public static List<TopicMapSourceIF> readSources(String config_file) {
    return readSources(config_file, new HashMap<String, String>(1));
  }

  /**
   * INTERNAL: Returns a collection containing the topic map sources
   * created by reading the configuration file.
   */
  public static List<TopicMapSourceIF> readSources(String config_file, Map<String, String> environ) {
    if (environ == null) {
      environ = new HashMap<String, String>(1);
    }
    // add CWD entry
    if (!environ.containsKey(CWD)) {
      File file = new File(config_file);
      if (!file.exists()) {
        throw new OntopiaRuntimeException("Config file '" + config_file +
                                          "' does not exist.");
      }
      environ.put(CWD, file.getParent());
    }

    return readSources(new InputSource(URIUtils.toURL(new File(config_file)).toString()), environ);
  }

  // ------------------------------------------------------------
  // internal helper method(s)
  // ------------------------------------------------------------
  
  private static List<TopicMapSourceIF> readSources(InputSource inp_source, Map<String, String> environ) {
    ConfigHandler handler = new ConfigHandler(environ);
    
    try {
      XMLReader parser = DefaultXMLReaderFactory.createXMLReader();
      parser.setContentHandler(handler);
      parser.setErrorHandler(new Slf4jSaxErrorHandler(log));
      parser.parse(inp_source);
    } catch (SAXParseException e) {
      String msg = "" + e.getSystemId() + ":" + e.getLineNumber() + ":" +
                   e.getColumnNumber() + ": " + e.getMessage();
      throw new OntopiaRuntimeException(msg, e);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
    return handler.sources;
  }

  // ------------------------------------------------------------
  // internal ContentHandler class
  // ------------------------------------------------------------
  
  static class ConfigHandler extends DefaultHandler {
    private Map<String, String> environ;
    //Map params = new HashMap();
    private List<TopicMapSourceIF> sources = new ArrayList<TopicMapSourceIF>();
    
    private TopicMapSourceIF source;
    
    ConfigHandler(Map<String, String> environ) {
      this.environ = environ;
    }
    
    @Override
    public void startElement(String uri, String name, String qName,
                              Attributes atts) throws SAXException {
      if ("source".equals(qName)) {
        // Clear source member
        source = null;
        try {
          ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
          source = (TopicMapSourceIF) Class.forName(atts.getValue("class"), true, classLoader).newInstance();
          String id = atts.getValue("id");
          if (id != null) {
            source.setId(id);
          }
          sources.add(source);
          //log.debug("Added source " + source + ".");
        } catch (ClassNotFoundException e) {
          log.error("Cannot find class " + e.getMessage());
        } catch (Exception e) {
          log.error("Exception: " + e.getClass().getName() + ": " + e.getMessage());
        }
      }
      else if ("param".equals(qName) && source != null) {
        String param_name = atts.getValue("name");
        String param_value = atts.getValue("value");
        Iterator<String> iter = environ.keySet().iterator();
        while (iter.hasNext()) {
          String environ_key = iter.next();
          param_value = StringUtils.replace(param_value, "${" + environ_key + "}", environ.get(environ_key));
        }

        try {
          BeanInfo bean_info = Introspector.getBeanInfo(source.getClass());
          PropertyDescriptor[] props = bean_info.getPropertyDescriptors();
          boolean found_property = false;
          for (int i = 0; i < props.length; i++) {
            //log.debug("property: " + props[i].getName());
            //System.out.println("P: " + props[i].getName() + " T: " + props[i].getPropertyType());
            if (props[i].getName().equals(param_name)) {
              Method setter = props[i].getWriteMethod();
              if (props[i].getPropertyType().equals(String.class)) {
                setter.invoke(source, new Object[] {param_value});
                found_property = true;
                break;
              }
              else if (props[i].getPropertyType().equals(boolean.class)) {
                setter.invoke(source, new Object[] {Boolean.parseBoolean(param_value)});
                found_property = true;
                break;
              }
            }
          }
          if (!found_property) {
            throw new SAXException("Cannot find property '" + param_name + "' " +
                                   "on source " + source);
          }
        } catch (IntrospectionException e) {
          throw new SAXException(e);      
        } catch (InvocationTargetException e) {
          throw new SAXException(e);      
        } catch (IllegalAccessException e) {
          throw new SAXException(e);      
        }
      }
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
      if ("source".equals(qName)) {     
        source = null;
      }
    }

  } 
}
