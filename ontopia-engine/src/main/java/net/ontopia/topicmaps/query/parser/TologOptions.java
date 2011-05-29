
package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.PropertyUtils;

/**
 * INTERNAL: Represents the properties set in a given tolog processing
 * context. There are three kinds of contexts: in a given query, for a
 * given query, and global defaults. This is represented as three
 * nested TologOptions objects.
 */
public class TologOptions {
  public static TologOptions defaults;
  private static Properties properties; // tolog.properties, if loaded
  static Logger log = LoggerFactory.getLogger(TologOptions.class.getName());

  private TologOptions parent;
  private Map<String, String> options;
  
  public TologOptions() {
    options = new HashMap<String, String>();
  }

  public TologOptions(TologOptions parent) {
    this.parent = parent;
    this.options = new HashMap<String, String>();
  }
  
  public boolean getBooleanValue(String name) {
    String value = options.get(name);
    if (value == null) {
      if (parent == null)
        return false; // FIXME: throw exception?
      return parent.getBooleanValue(name);
    } else
      return value.equalsIgnoreCase("true");
  }

  public void setOption(String name, String value) {
    options.put(name, value);
  }

  // --- LOADING tolog.properties

  public void loadProperties() {
    if (properties == null) {
      try {
        properties = PropertyUtils.loadPropertiesFromClassPath("tolog.properties");
      } catch (IOException e) {
        log.warn("Couldn't load tolog.properties", e);
      }

      if (properties == null) // avoid a reload
        properties = new Properties(); 
    }

    // copy across properties
    for (Object k : properties.keySet()) {
      String key = (String) k;
      String value = (String) properties.get(key);
      options.put(key, value);
    }
  }

  // --- DEFAULT OPTIONS
    
  static {
    defaults = new TologOptions();
    defaults.setOption("optimizer.inliner", "true");
    defaults.setOption("optimizer.reorder", "true");
    defaults.setOption("optimizer.reorder.predicate-based", "true");
    defaults.setOption("optimizer.typeconflict", "true");
    defaults.setOption("optimizer.hierarchy-walker", "true");             
    defaults.setOption("optimizer.prefix-search", "true");
    defaults.setOption("optimizer.recursive-pruner", "true"); // rules only
    defaults.setOption("compiler.typecheck", "true"); // queryanalyzer
    // optimizer.role-player-type: default depends on implementation
    // optimizer.next-previous: ditto.
    // both set in QueryProcessor constructor
  }
}
