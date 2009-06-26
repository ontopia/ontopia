
// $Id: Module.java,v 1.26 2007/09/10 13:35:11 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.net.URL;

import net.ontopia.utils.ontojsp.JSPTreeNodeIF;

import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.topicmaps.nav2.core.ModuleReaderIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.ModuleReader;
  
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: ModuleIF implementation that reads functions from an XML
 * resource.
 */
public class Module implements ModuleIF {

  // initialization of log facility
  private static Logger log = LoggerFactory.getLogger(Module.class.getName());

  // types of module encodings
  public static final String TYPE_ENCRYPTED = "EncryptedModuleReader";
  public static final String TYPE_PLAIN = "PlainModuleReader";
  
  // members
  private URL location;
  private String readerType;
  private long resourceLastModReadIn;
  private Collection functions;


  public Module(URL location) {
    this(location, TYPE_PLAIN);
  }

  public Module(URL location, String readerType) {
    this.location = location;
    this.readerType = readerType;
    this.functions = new HashSet();
  }
  
  // Implementation of ModuleIF
  
  public URL getURL() {
    return location;
  }
  
  public Collection getFunctions() {
    return functions;
  }

  public synchronized void addFunction(FunctionIF func) {
    functions.add(func);
  }

  public synchronized void clearFunctions() {
      functions.clear();
  }
  
  public boolean hasResourceChanged() {
    // Compare the current modification time with the previous one.
    return (getModificationDate() > resourceLastModReadIn);
  }

  public void readIn() throws NavigatorRuntimeException {
    this.clearFunctions();
    Map funcs = null;

    if (readerType == null || readerType.equals("")) {
      readerType = TYPE_PLAIN;
      log.debug("No reader type, falling back to " + readerType);
    }
    ModuleReaderIF reader = null;
    if (readerType.equalsIgnoreCase(TYPE_ENCRYPTED)
        || readerType.equalsIgnoreCase(TYPE_PLAIN)) {
      log.info("Read in Module from "+location+" (using "+readerType+").");

      // [[ new InputStreamReader(stream)
      // url.openConnection().getInputStream()
      reader = new ModuleReader(readerType.equalsIgnoreCase(TYPE_ENCRYPTED));
    } else
      throw new NavigatorRuntimeException("Unknown module reader '" +
                                          readerType +
                                          "' defined in application.xml");
    try {
      funcs = reader.read(location.openConnection().getInputStream());
      resourceLastModReadIn = getModificationDate();
    } catch (IOException e) {
      log.error("Error reading the module : " + e);
      throw new NavigatorRuntimeException("Error reading module.", e);
    } catch (SAXParseException e) {
      log.error("Error parsing the module: " + e);
      throw new NavigatorRuntimeException("Error reading module '"+location+
                                          "':" + e.getLineNumber() + ":" +
                                          e.getColumnNumber(), e);
    } catch (SAXException e) {
      log.error("Error parsing the module: " + e);
      throw new NavigatorRuntimeException("Error reading module '"+location+
                                          "'.", e);
    }

    if (log.isDebugEnabled())
      log.debug("Module.readIn - funcs:" + funcs);
    
    // create functions and assign them to the module
    Iterator it = funcs.keySet().iterator();
    while (it.hasNext()) {
      String functionName = (String) it.next();
      FunctionIF function = (FunctionIF) funcs.get(functionName);
      this.addFunction(function);
      if (log.isInfoEnabled())
        log.info(" - registered function: " + function.toString());
    }
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("[Module (type: ").append(readerType);
    sb.append(") location: ").append(location);
    sb.append("]");
    return sb.toString();
  }

  // helper method
  
  private long getModificationDate() {
    File file = new File(location.getFile());
    return file.lastModified();
  }
  
}
