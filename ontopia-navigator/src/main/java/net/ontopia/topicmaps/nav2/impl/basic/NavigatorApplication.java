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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.SharedStoreRegistry;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.plugins.PluginConfigFactory;
import net.ontopia.topicmaps.nav2.plugins.PluginIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorConfigFactory;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * INTERNAL: Basic Implementation of interface NavigatorApplicationIF to
 * store all handles to application-wide configuration holders needed
 * by the navigator framework.
 * <p>
 * Note: The default behaviour if no plug-ins dir is specified (in web.xml)
 * that there will be no plug-ins available.
 */
public final class NavigatorApplication implements NavigatorApplicationIF {

  // initialization of log facility
  private static final Logger log = LoggerFactory.getLogger(NavigatorApplication.class.getName());

  // the name of a plug-in specification file in a plug-in directory
  private static final String PLUGIN_SPEC = "plugin.xml";
  
  // topic map repository
  private TopicMapRepositoryIF repository;
  private boolean localRepository;
  private String repositoryId;
  
  // members
  private NavigatorConfigurationIF navConfig;
  // key: shortcut (String), value: fqcn (String)
  private Map instances = new HashMap();
  // key: filename (String), value: module (ModuleIF)
  private Map modules = new HashMap();
  private ServletContext servlet_context;
  
  /**
   * INTERNAL: Default Constructor.
   *
   * @param context - The ServletContext object, which is
   *        needed to retrieve the context and configuration information
   *        about this web application.
   */
  public NavigatorApplication(ServletContext context) {
    this.servlet_context = context;
    servlet_context.log("Start constructing NavigatorApplication object.");
    init();
    log.info("NavigatorApplication initialized for '" + getName() + "'.");
  }

  private InputStream getInputStream(String name) throws IOException {
    // adapted from StreamUtils.getInputStream(String)
    InputStream istream;
    if (name.startsWith("classpath:")) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring("classpath:".length());
      istream = cl.getResourceAsStream(resourceName);
      if (istream == null)
        throw new IOException("Resource '" + resourceName + "' not found through class loader.");
      log.debug("File loaded through class loader: " + name);
    } else if (name.startsWith("file:")) {
      File f =  new File(name.substring("file:".length()));
      if (f.exists()) {
        log.debug("File loaded from file system: " + name);
        istream = new FileInputStream(f);
      } else
        throw new IOException("File '" + f + "' not found.");
    } else {
      String absname = makeAbsolute(name);
      File f = new File(absname);
      if (f.exists()) {
        log.debug("File loaded from file system: " + absname);
        istream = new FileInputStream(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        istream = cl.getResourceAsStream(name);
        if (istream != null)
          log.debug("File loaded through class loader: " + name);
      }
    }
    return istream;    
  }
  
  private String makeAbsolute(String filename) {
    if (filename != null && filename.length() > 0 && !(new java.io.File(filename).isAbsolute()))
      // Filename is relative to the webapp root directory
      return servlet_context.getRealPath("") + "/" + filename;
    else
      return filename;
  }

  // ------------------------------------------------------------
  // Implementation of NavigatorApplicationIF
  // ------------------------------------------------------------

  /**
   * @return Display name of the web application {@link
   *         javax.servlet.ServletContext#getServletContextName()}.
   */
  @Override
  public String getName() {
    return JSPEngineWrapper.getServletContextName(servlet_context);
  }
  
  @Override
  public NavigatorConfigurationIF getConfiguration() {
    return navConfig;
  }

  @Override
  public TopicMapRepositoryIF getTopicMapRepository() {
      return repository;
  }

  @Override
  public TopicMapIF getTopicMapById(String topicmapId)
    throws NavigatorRuntimeException {
    return getTopicMapById(topicmapId, false);
  }
  
  @Override
  public TopicMapIF getTopicMapById(String topicmapId, boolean readonly)
    throws NavigatorRuntimeException {
    
    // use local or jndi repository (for backwards compatibility);
    TopicMapReferenceIF ref = repository.getReferenceByKey(topicmapId);
    if (ref == null) 
      throw new NavigatorRuntimeException("Topic map with id '" + topicmapId +
                                          "' not found.");      
    TopicMapStoreIF store;
    try {
      store = ref.createStore(readonly);
    } catch (java.io.IOException e) {
      throw new NavigatorRuntimeException("Problems occurred when creating topic map store '" + 
                                          topicmapId + "'", e);
    }
    
    if (readonly)
      log.debug("Got RO store: " + store);
    else
      log.debug("Got RW store: " + store);

    return store.getTopicMap();
  }

  @Override
  public void returnTopicMap(TopicMapIF topicmap) {
    TopicMapStoreIF store = topicmap.getStore();
    log.debug("Returning store: " + store);
    store.close();
  }

  @Override
  public String getTopicMapRefId(TopicMapIF topicmap) {
    TopicMapStoreIF store = topicmap.getStore();
    TopicMapReferenceIF ref = store.getReference();
    return (ref == null ? null : getTopicMapRepository().getReferenceKey(ref));
  }

  @Override
  public Object getInstanceOf(String classname)
    throws NavigatorRuntimeException {

    String fqcn = null;
    
    // First try if this is an shortcut for a classname
    fqcn = navConfig.getClass(classname);
    // ...otherwise fallback to interpret parameter as full-qualified
    if (fqcn == null || fqcn.equals(""))
      fqcn = classname;

    // lookup if we have an instance available for this class
    Object instance = instances.get(fqcn);
    // ... otherwise try to create a new instance of it
    if (instance == null) {
      try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class klass = Class.forName(fqcn, true, classLoader);
        instance = klass.newInstance();
        instances.put(fqcn, instance);
      } catch (ClassNotFoundException e) {
        throw new NavigatorRuntimeException("Class '" + fqcn + "' not found.", e);
      } catch (LinkageError e) {
        String msg = "Unable to create instance of class '" + fqcn + "': " + e;
        throw new NavigatorRuntimeException(msg, e);
      } catch (InstantiationException e) {
        String msg = "Unable to create instance of class '" + fqcn + "': " + e;
        throw new NavigatorRuntimeException(msg, e);
      } catch (IllegalAccessException e) {
        String msg = "Unable to create instance of class '" + fqcn + "': " + e;
        throw new NavigatorRuntimeException(msg, e);
      }
    }
  
    return instance;
  }

  /**
   * Gets module from internal cache, if it is not in or should be
   * refreshed the module resource is loaded in again.
   */
  @Override
  public ModuleIF getModule(URL location)
    throws NavigatorRuntimeException {
    
    ModuleIF module = null;
    Object obj_module = modules.get(location);
    // --- no appropiate module found, create a new one
    if (obj_module == null) {
      String reader_type = navConfig
        .getProperty(NavigatorConfigurationIF.MODULE_READER);
      module = new Module(location, reader_type);
      module.readIn();
      // put it into hashmap
      modules.put(location, module);
    }
    // --- reuse same module object instance
    else {
      module = (ModuleIF) obj_module;
      // check if modified in the meantime
      if (navConfig.getProperty(NavigatorConfigurationIF.CHECK_FOR_CHANGED_MODULES,
                                "false").equals("true")) {
        if (module.hasResourceChanged()) {
          log.info("The module file has changed and is being reloaded.");
          module.readIn();
        }
      }
    }
    return module;
  }

  @Override
  public void close() {
    if (repository != null && localRepository) {
      try {
        // only close local repositories
        repository.close();
      } catch (Throwable t) {
        log.error("Problems occurred when closing navigator application.", t);
      }      
    }
  }
  
  // ------------------------------------------------------------
  // internal helper methods
  // ------------------------------------------------------------

  /**
   * INTERNAL: Sets up needed instances for constructors.
   */
  private void init() {    
    // --- set up logging with configuration from property file
    configureLogging();

    // --- read in application configuration
    readInAppConfig();

    // --- read in topicmap source configuration
    loadTopicMapRepository();

    // --- scan for plug-in configuration files and read them in
    readAndSetPlugins();
  }

  /**
   * INTERNAL: Configures the logging facility (Log4J) with the help
   * of the property file containing configuration issues..
   */
  private synchronized void configureLogging() {
    //! Does the removal of this code cause probelems?
    //! SLF4J is not meant to be configured
    
    /*
    
    // load in properties
    String filePathLog4J = servlet_context.getInitParameter(LOG4J_CONFIG_KEY);
    if (filePathLog4J == null)
      log.info("Logging not forced.");
    else {
      Properties props = new Properties();      
      servlet_context.log("start to load log4j configuration from " + filePathLog4J);
      try {
        props.load(getInputStream(filePathLog4J));
      } catch (IOException ioe) {
        throw new OntopiaRuntimeException("Unable to load log configuration.", ioe);
      }
      // pre-cat the real directory to all file properties
      Enumeration pnames = props.propertyNames();
      while (pnames.hasMoreElements()) {
        String pname = (String) pnames.nextElement();
        String pval = props.getProperty(pname);
        if (pname.endsWith(".File")) {
          try {
            // check if directory is available
            File dir = (new File(pval)).getParentFile();
            if (dir != null && !dir.exists()) {
              dir.mkdirs();
              servlet_context.log("Created log directory " + dir);
            }
            props.setProperty(pname, pval);
          } catch (SecurityException ioe) {
            servlet_context.log("Not allowed to create log dir, " + ioe);
          }
        }
      }
      // finally configure Log4J with modified paths
      PropertyConfigurator.configure(props);
      log.info("Configured Logging with properties from " + filePathLog4J);
    }
    */
  }
  
  /**
   * INTERNAL: Reads in one application configuration XML file.
   */
  private synchronized void readInAppConfig() {
    try {
      String filePathAppConfig = servlet_context.getInitParameter(APP_CONFIG_KEY);
      if (filePathAppConfig == null)
        filePathAppConfig = APP_CONFIG_DEFAULT_VALUE;
      log.info("Start to load application configuration from " + filePathAppConfig);
      InputStream istream = getInputStream(filePathAppConfig);
      if (istream != null) {
        this.navConfig = NavigatorConfigFactory.getConfiguration(istream);
        return;
      }
    }
    catch (SAXParseException e) {
      log.error("Couldn't parse application configuration.", e);
      this.navConfig = new BrokenNavigatorConfiguration("Couldn't parse application configuration: " + e.getMessage() + " at: " + e.getSystemId() + ":" + e.getLineNumber() + ":" + e.getColumnNumber());
      return;
    }
    catch (SAXException e) {
      log.error("Couldn't parse application configuration.", e);
      this.navConfig = new BrokenNavigatorConfiguration("Couldn't parse application configuration: " + e.getMessage());
      return;
    } catch (IOException e) {
      log.error("Couldn't read in application configuration: " + e.getMessage());
    }

    this.navConfig = new NavigatorConfiguration();
  }
  
  /**
   * INTERNAL: Loads the topic map repository, which stores
   * information about which topicmaps should be made available to the
   * web application.
   */
  private synchronized void loadTopicMapRepository() {
    // NOTE: if neither of the two properties are specified we'll use the default repository
    
    // get shared registry from JNDI
    String jndi_repository = servlet_context.getInitParameter(JNDI_REPOSITORY_KEY);
    if (jndi_repository != null) {
      SharedStoreRegistry ssr = lookupSharedStoreRegistry(jndi_repository);
      this.repository = ssr.getTopicMapRepository();
      this.localRepository = false;
      log.warn("Navigator application '" + getName() + "' loaded topic maps repository: " + this.repository + " from JNDI '" + jndi_repository + "'. WARNING: you may not want to do this.");
      return; // we're done
    }

    // load topic map repository from specific file
    String source_config = servlet_context.getInitParameter(SOURCE_CONFIG_KEY);
    if (source_config != null) {
      Map<String, String> environ = new HashMap<String, String>(Collections.singletonMap("WEBAPP", servlet_context.getRealPath("")));
      if (source_config.startsWith("classpath:")) {
        this.repository = TopicMaps.getRepository(source_config, environ);
        log.info("Navigator application '" + getName() + "' loaded topic maps repository: " + this.repository + " from file '" + source_config + "'");        
      } else {
        String source_config_file = makeAbsolute(source_config);
        this.repository = TopicMaps.getRepository("file:" + source_config_file, environ);
        log.info("Navigator application '" + getName() + "' loaded topic maps repository: " + this.repository + " from file '" + source_config_file + "'");        
      }
      this.localRepository = true;
      return; // we're done
    }

    // application can specify specific repository id
    this.repositoryId = servlet_context.getInitParameter(TOPICMAPS_REPOSITORY_ID);
    if (this.repositoryId != null) {
      this.repository = TopicMaps.getRepository(this.repositoryId);
      log.info("Navigator application '" + getName() + "' will use shared topic maps repository with id '" + this.repositoryId + "'");
      return; // we're done
    }
    
    // fallback to default repository
    this.repository = TopicMaps.getRepository();
    log.info("Navigator application '" + getName() + "' will use default shared topic maps repository");
  }
  
  /**
   * INTERNAL: Looks up the SharedStoreRegistry in JNDI.
   */
  public static SharedStoreRegistry lookupSharedStoreRegistry(String jndi_name) {
    log.info("Looking up shared store registry in JNDI '" + jndi_name + "'");
    
    // lookup in jndi using Tomcat approach
    SharedStoreRegistry ssr = (SharedStoreRegistry)lookupJNDI("java:comp/env/" + jndi_name);
    
    if (ssr == null)
      // lookup in jndi using oc4j approach
      ssr = (SharedStoreRegistry)lookupJNDI("java:comp/resource/" +  jndi_name + "/");
    
    if (ssr == null)
      throw new OntopiaRuntimeException("Couldn't find shared repository " +
                                        jndi_name);
    return ssr;
  }
  
  private static SharedStoreRegistry lookupJNDI(String name) {
    try {
      log.info("Looking up JNDI name: " + name);
      Context ctx = new InitialContext();
      SharedStoreRegistry ssr = (SharedStoreRegistry) ctx.lookup(name);
      if (ssr == null)
        throw new OntopiaRuntimeException("No JNDI shared repository named " +
                                          name + " found");
        
      return ssr;
    } catch (NamingException e) {
      return null;
    }
  }
  
  /**
   * INTERNAL: Processes list of plug-in specification resources
   * starting from the given base path and add all therein contained
   * plug-ins to navigator configuration.
   */
  private synchronized void readAndSetPlugins() {
    String pluginsRootURI = servlet_context.getInitParameter(PLUGINS_ROOTDIR_KEY);
    // it is allowed to specify no plug-in directory
    if (pluginsRootURI == null) return;
    
    Collection pluginspecs = getPluginSpecifications(makeAbsolute(pluginsRootURI));
    Iterator it = pluginspecs.iterator();
    while (it.hasNext()) {
      File specfile = (File) it.next();
      String plugindir = specfile.getParent();
      try {
        // Reads in one plug-in XML instance and generate PluginIF
        // instances from it.
        Collection plugins = PluginConfigFactory.getPlugins(new FileInputStream(specfile), plugindir, pluginsRootURI);
        // Loop over the plug-in inside one directory
        Iterator iter = plugins.iterator();
        while (iter.hasNext()) {
          PluginIF plugin = (PluginIF) iter.next();
          if (plugin != null) {
            log.info("  * found " + plugin);
            navConfig.addPlugin( plugin );
          }
        } // while
      } catch (FileNotFoundException e) {
        throw new OntopiaRuntimeException("Could not find plugin directory: " + plugindir);
      }
    } // while it
    log.info("Loaded plug-in configuration.");
  }

  /**
   * INTERNAL: Scans directories for appropiate xml instances which
   * specify plug-in properties.
   *
   * @return Collection of File objects referencing the specification files.
   */
  private Collection getPluginSpecifications(String startPath) {
    File directory = new File(startPath);
    if (!directory.exists()) {
      log.info("Plugins directory does not exist '" + startPath + "'");
      return Collections.EMPTY_SET;
    }
    
    log.info("Scanning directory '" + startPath + "' for plug-ins.");    
    File[] files = directory.listFiles();

    // contains String with path to plugin.xml in plug-in dirs
    List pluginspecs = new ArrayList();
    
    for (int i=0; i < files.length; i++) {
      File plugindir = files[i];
      if (plugindir.isDirectory()) {
        File pluginspec = new File(plugindir, PLUGIN_SPEC);
        if (pluginspec.exists()) {
          pluginspecs.add(pluginspec);
        }
      }
    }
    return pluginspecs;
  }

}
