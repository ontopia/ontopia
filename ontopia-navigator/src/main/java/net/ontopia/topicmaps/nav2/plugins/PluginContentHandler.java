
package net.ontopia.topicmaps.nav2.plugins;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.xml.sax.*;
import net.ontopia.xml.SAXTracker;
import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A Content Handler for reading in a Plug-in Configuration
 * file (plugin.xml) which contains detailed information about one (or
 * more) Plug-in(s).
 *
 * @see net.ontopia.topicmaps.nav2.plugins.PluginIF
 */
public class PluginContentHandler extends SAXTracker {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(PluginContentHandler.class.getName());

  // constants
  public static final String PLUGINS_ROOTDIR_PLACEHOLDER =
    "${" + NavigatorApplicationIF.PLUGINS_ROOTDIR_KEY + "}";
  
  // member
  private Collection plugins;
  private PluginIF cplugin;
  // This does not contain the context-path, but only "/plugins/"
  private String pluginsRootURI;
    
  private String param_name;

  public PluginContentHandler(String pluginsRootURI) {
    this.pluginsRootURI = pluginsRootURI;
    keepContentsOf("title");
    keepContentsOf("descr");
    keepContentsOf("target");
    keepContentsOf("activated");
    keepContentsOf("uri");
    keepContentsOf("parameter");

    plugins = new ArrayList();
  }

  public Collection getPlugins() {
    return plugins;
  }
  
  // --------------------------------------------------------------
  // override methods from SAXTracker
  // --------------------------------------------------------------
  
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) throws SAXException {
    super.startElement(nsuri, lname, qname, attrs);

    if (qname == "plugin") {
      String klass = attrs.getValue("class");
      if (klass == null)
        cplugin = new DefaultPlugin();
      else
        cplugin = createPlugin(klass);

      if (cplugin == null)
        return; // createPlugin may fail...
      
      cplugin.setId(attrs.getValue("id"));
      String str_groups = attrs.getValue("groups");
      // split group containing string into elements
      if (str_groups != null && !str_groups.equals("")) {
        String[] grpArray = StringUtils.split(str_groups, ",");
        List groups = new ArrayList(grpArray.length);
        for (int i = 0; i < grpArray.length; i++) 
          groups.add(grpArray[i].trim());
        cplugin.setGroups(groups);
      }
        
    } else if (qname == "parameter") {
      param_name = attrs.getValue("name");
      if (attrs.getValue("value") != null)
        cplugin.setParameter(attrs.getValue("name"),
                             attrs.getValue("value"));
    }
  }
  
  public void endElement(String nsuri, String lname, String qname) throws SAXException {
    super.endElement(nsuri, lname, qname);

    if (qname == "plugin")
      plugins.add(cplugin);
    else if (qname == "title")
      cplugin.setTitle(content.toString());
    else if (qname == "descr")
      cplugin.setDescription(content.toString());
    else if (qname == "target")
      cplugin.setTarget(content.toString());
    else if (qname == "uri")
      cplugin.setURI(processURI(content.toString()));
    else if (qname == "activated") {
      String value = content.toString();

      if (cplugin.getState() != PluginIF.ERROR) {
        if (value == null || !value.equals("yes"))
          cplugin.setState(PluginIF.DEACTIVATED);
        else
          cplugin.setState(PluginIF.ACTIVATED);
      }
    }
    else if (qname == "parameter") {
      // If parameter hasn't yet been set use the content of the parameter element.
      if (cplugin.getParameter(param_name) == null)
        cplugin.setParameter(param_name, content.toString());
      // Reset parameter name member
      param_name = null;
    }
  }
  
  // --------------------------------------------------------------
  // internal helper methods
  // --------------------------------------------------------------

  private PluginIF createPlugin(String klass) {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class pclass = Class.forName(klass, true, classLoader);
      return (PluginIF) pclass.newInstance();
    }
    catch (ClassNotFoundException e){
      log.error("Couldn't find class of plugin " + klass +
                ": " + e.getMessage());
      PluginIF plugin = new DefaultPlugin();
      plugin.setState(PluginIF.ERROR);
      return plugin;
    }
    catch (InstantiationException e){
      log.error("Couldn't instantiate plugin (" + klass +
                "): " + e.getMessage());
    }      
    catch (IllegalAccessException e){
      log.error("Couldn't access plugin instance " + klass +
                ": " + e.getMessage());
    }
    catch (NoClassDefFoundError e){
      log.error("Couldn't find class definition of plugin " +
                klass + ": " + e.getMessage());
    }
    return null;
  }

  /**
   * INTERNAL: Process specified URI string and replace placeholder
   * (<code>"${" + NavigatorApplicationIF.PLUGINS_ROOTDIR_KEY +
   * "}"</code>) with actual plugins root directory.
   */
  private String processURI(String orig_uri) {
    String uri = orig_uri;
    if (uri.indexOf(PLUGINS_ROOTDIR_PLACEHOLDER) >= 0) {
      uri = StringUtils.replace(uri, PLUGINS_ROOTDIR_PLACEHOLDER,
                                pluginsRootURI);
    }
    return uri;
  }
  
}
