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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Utilities for working with streams and readers.
 * @since 1.3.3
 */
public class StreamUtils {
  public static final String CLASSPATH_PREFIX = "classpath:";
  public static final String FILE_PREFIX = "file:";
  private static final String MSG_FILE_LOADED_THROUGH_CLASS_LOADER = "File loaded through class loader: ";
  private static final String MSG_FILE_LOADED_FROM_FILE_SYSTEM = "File loaded from file system: ";
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(StreamUtils.class.getName());
  
  /**
   * INTERNAL: Same as getInputStream(null, name);
   *
   * @since 3.4.3
   */
  public static InputStream getInputStream(String name) throws IOException {
    return getInputStream(null, name);
  }

  /**
   * INTERNAL: Returns an input stream for the given url. Supports
   * file: and classpath:. If no scheme given then file system will be
   * checked before classpath. Exception will be thrown if resource is
   * not found when scheme is given. If no scheme was given null is
   * returned. File references will be interpreted relative to basedir.
   * If basedir is null, it will be ignored.
   *
   * @since 5.1.1
   */
  public static InputStream getInputStream(File basedir, String name)
    throws IOException {
    InputStream istream;
    if (name.startsWith(CLASSPATH_PREFIX)) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring(CLASSPATH_PREFIX.length());
      istream = cl.getResourceAsStream(resourceName);
      if (istream == null) {
        throw new IOException("Resource '" + resourceName + "' not found through class loader.");
      }
      log.debug(MSG_FILE_LOADED_THROUGH_CLASS_LOADER + name);
    } else if (name.startsWith(FILE_PREFIX)) {
      File f = makeFile(basedir, name.substring(FILE_PREFIX.length()));
      if (f.exists()) {
        log.debug(MSG_FILE_LOADED_FROM_FILE_SYSTEM + name);
        istream = new FileInputStream(f);
      } else {
        throw new IOException("File '" + f + "' not found.");
      }
    } else if (name.startsWith("jar:")) {
      return new URL(name).openStream();
    } else {
      File f = makeFile(basedir, name);
      if (f.exists()) {
        log.debug(MSG_FILE_LOADED_FROM_FILE_SYSTEM + name);
        istream = new FileInputStream(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        istream = cl.getResourceAsStream(name);
        if (istream != null) {
          log.debug(MSG_FILE_LOADED_THROUGH_CLASS_LOADER + name);
        }
      }
    }
    return istream;
  }

  private static File makeFile(File basedir, String name) {
    if (basedir == null) {
      return new File(name);
    } else {
      return new File(basedir, name);
    }
  }
  
  /**
   * INTERNAL: Returns an URL for the given resource name. 
   * 
   * The order of resolving is: <ol>
   * <li>If name starts with 'classpath:' a resource on the classpath will be resolved.
   * If the resource cannot be found, a 'FileNotFoundException' is thrown</li>
   * <li>If name starts with 'file:' a resource on the filesystem will be resolved.
   * If the file cannot be found, a 'FileNotFoundException' is thrown</li>
   * <li>if name starts with 'jar:' a jar-URL will be created and returned. Only fails if name is not a valid URL</li>
   * <li>name is treated as a file system path, if a file exists an URL to it will be returned</li>
   * <li>name is treated as a classpath resource, if it is a valid URL and the resource exists, it will be returned.</li>
   * <li>null is returned</li>
   * </ol>
   * 
   * @since 3.4.3
   */
  public static URL getResource(String name) throws IOException {
    URL url;
    if (name.startsWith(CLASSPATH_PREFIX)) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      String resourceName = name.substring(CLASSPATH_PREFIX.length());
      url = cl.getResource(resourceName);
      if (url == null) {
        throw new FileNotFoundException("Resource '" + resourceName + "' not found through class loader.");
      }
      log.debug(MSG_FILE_LOADED_THROUGH_CLASS_LOADER + name);
    } else if (name.startsWith(FILE_PREFIX)) {
      File f =  new File(name.substring(FILE_PREFIX.length()));
      if (f.exists()) {
        log.debug(MSG_FILE_LOADED_FROM_FILE_SYSTEM + name);
        url = URIUtils.toURL(f);
      } else {
        throw new IOException("File '" + f + "' not found.");
      }
    } else if (name.startsWith("jar:")) {
      return new URL(name);
    } else {
      File f = new File(name);
      if (f.exists()) {
        log.debug(MSG_FILE_LOADED_FROM_FILE_SYSTEM + name);
        url = URIUtils.toURL(f);
      } else {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        url = cl.getResource(name);
        if (url != null) {
          log.debug(MSG_FILE_LOADED_THROUGH_CLASS_LOADER + name);
        }
      }
    }
    return url;
  }
}
