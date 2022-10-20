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
  private static final Logger log = LoggerFactory.getLogger(TologOptions.class.getName());
  private static final String TRUE = Boolean.TRUE.toString();

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
      if (parent == null) {
        return false; // FIXME: throw exception?
      }
      return parent.getBooleanValue(name);
    } else {
      return Boolean.parseBoolean(value);
    }
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

      if (properties == null) { // avoid a reload
        properties = new Properties();
      } 
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
    defaults.setOption("optimizer.inliner", TRUE);
    defaults.setOption("optimizer.reorder", TRUE);
    defaults.setOption("optimizer.reorder.predicate-based", TRUE);
    defaults.setOption("optimizer.typeconflict", TRUE);
    defaults.setOption("optimizer.hierarchy-walker", TRUE);             
    defaults.setOption("optimizer.prefix-search", TRUE);
    defaults.setOption("optimizer.recursive-pruner", TRUE); // rules only
    defaults.setOption("compiler.typecheck", TRUE); // queryanalyzer
    // optimizer.role-player-type: default depends on implementation
    // optimizer.next-previous: ditto.
    // both set in QueryProcessor constructor
  }
}
