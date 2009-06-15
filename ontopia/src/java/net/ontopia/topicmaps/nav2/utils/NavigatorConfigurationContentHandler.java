// $Id: NavigatorConfigurationContentHandler.java,v 1.7 2005/02/22 15:46:52 grove Exp $

package net.ontopia.topicmaps.nav2.utils;

import net.ontopia.xml.SAXTracker;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.impl.basic.NavigatorConfiguration;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

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
  static Logger log = Logger
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
