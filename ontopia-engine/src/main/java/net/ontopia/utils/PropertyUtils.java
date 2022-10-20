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

package net.ontopia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/** 
 * INTERNAL: Utility class for handling properties and their values.
 */

public class PropertyUtils {

  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class.getName());
  
  /**
   * INTERNAL: Helper method used to get better error messages with
   * less typing.
   */
  public static String getProperty(Map<String, String> properties, String name) {
    return getProperty(properties, name, true);
  }

  /**
   * INTERNAL: Helper method used to get better error messages with
   * less typing.
   */
  public static String getProperty(Map<String, String> properties, String name, boolean required) {
    String value = properties.get(name);
    if ((value == null) && required) {
        throw new IllegalArgumentException("No value for required property '" + name + "'");
    }
    return value;
  }
  
  public static boolean isTrue(String property_value, boolean default_value) {
    return BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(property_value), default_value);
  }
  
  /**
   * INTERNAL: Returns the property value as an int. If the value is
   * not set or any problems occur the default value is returned.
   */
  public static int getInt(String property_value, int default_value) {
    if (property_value == null) {
      return default_value;
    } else
      try {
        return getInt(property_value);
      } catch (NumberFormatException e) {
        log.warn(e.toString());
        return default_value;
      }
  }

  public static int getInt(String property_value) throws NumberFormatException {
    return Integer.parseInt(property_value);
  }

  /**
   * INTERNAL; Reads properties from a file. 
   */
  public static Properties loadProperties(File propfile) throws IOException {
    if (!propfile.exists()) {
      throw new OntopiaRuntimeException("Property file '" + propfile.getPath() + "' does not exist.");
    }
    
    // Load properties from file
    Properties properties = new Properties();
    properties.load(new FileInputStream(propfile));
    return properties;
  }

  public static Properties loadPropertiesFromClassPath(String resource) throws IOException {
    // Load properties from classpath
    ClassLoader cloader = PropertyUtils.class.getClassLoader();
    Properties properties = new Properties();
    InputStream istream = cloader.getResourceAsStream(resource);
    if (istream == null) {
      return null;
    }
    properties.load(istream);
    return properties;
  }

  public static Properties loadProperties(InputStream istream) throws IOException {
    Properties properties = new Properties();
    properties.load(istream);
    return properties;
  }

  public static Map<String, String> toMap(Properties properties) {
    Map<String, String> result = new HashMap<String, String>(properties.size());
    for (String key : properties.stringPropertyNames()) {
      result.put(key, properties.getProperty(key));
    }
    return result;
  }
}
