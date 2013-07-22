/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.utils;

import net.ontopia.xml.SAXTracker;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.impl.basic.NavigatorConfiguration;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A Content Handler for reading in an application
 * configuration file (default name: <code>application.xml</code>,
 * default location: <code>WEB-INF/config</code>).
 *
 * @see net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF
 * @see net.ontopia.topicmaps.nav2.impl.basic.NavigatorConfiguration
 */
public class NavigatorConfigurationContentHandler extends SAXTracker {

  // Define a logging category.
  static Logger log = LoggerFactory
    .getLogger(NavigatorConfigurationContentHandler.class.getName());

  // member
  private NavigatorConfiguration navConfig;

  /**
   * INTERNAL: default constructor.
   */
  public NavigatorConfigurationContentHandler() {
    super();
  }

  public NavigatorConfigurationIF getNavigatorConfiguration() {
    return navConfig;
  }
  
  // --------------------------------------------------------------
  // override methods from SAXTracker
  // --------------------------------------------------------------

  public void startDocument() throws SAXException {
    super.startDocument();
    navConfig = new NavigatorConfiguration();
    log.debug("create new navConfig object.");    
  }
  
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) throws SAXException {
    super.startElement(nsuri, lname, qname, attrs);

    // ==== TODO
    log.debug("startElement: q("+qname+") l("+lname+")");
    
    if (qname == "configuration") {
      // TODO: use attribute "type" 
      //       for distinguishing between app and topicmap config
      // String configType = attrs.getValue("type");
      // --- if (configType.equals("application"))
    }
    else if (qname == "autoload") {
      // should be embedded in <autoloads>
      navConfig.addAutoloadTopicMap(attrs.getValue("topicmapid"));
      log.debug("added autoload topic map.");
    }
    else if (qname == "class") {
      // should be embedded in <classmap>
      navConfig.addClass(attrs.getValue("shortcut"), attrs.getValue("fullname"));
      log.debug("added class mapping.");
    }
    else if (qname == "property") {
      // should be embedded in <properties>
      navConfig.addProperty(attrs.getValue("name"), attrs.getValue("value"));
      log.debug("added property.");
    }
    else if (qname == "model") {
      // should be embedded in <mvs>
      navConfig.addModel(attrs.getValue("name"), attrs.getValue("title"),
                         (attrs.getValue("default") != null) ?
                         attrs.getValue("default").equalsIgnoreCase("yes") : false);
      log.debug("added model.");
    }
    else if (qname == "view") {
      // should be embedded in <mvs>
      navConfig.addView(attrs.getValue("name"), attrs.getValue("title"),
                        (attrs.getValue("default") != null) ?
                        attrs.getValue("default").equalsIgnoreCase("yes") : false);
      log.debug("added view.");
    }
    else if (qname == "skin") {
      // should be embedded in <mvs>
      navConfig.addSkin(attrs.getValue("name"), attrs.getValue("title"),
                        (attrs.getValue("default") != null) ?
                        attrs.getValue("default").equalsIgnoreCase("yes") : false);
      log.debug("added skin.");
    }
  }
  
  public void endElement(String nsuri, String lname, String qname) throws SAXException {
    super.endElement(nsuri, lname, qname);
  }
  
}
