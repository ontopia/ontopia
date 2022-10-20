/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to load dynamic services from classpath.
 * @since 5.4.0
 */
public class ServiceUtils {

  private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);

  public static final String SERVICES_ROOT = "META-INF/services/";

  /**
   * Calls {@link #loadServices(java.lang.Class, java.lang.String)} with resource as the
   * default location: META-INF/services/[classname].
   * @param <T> The type of the services to load
   * @param clazz The class that the services have to implement
   * @return The service instances found on the classpath
   * @throws IOException 
   */
  public static <T> Set<T> loadServices(Class<T> clazz) throws IOException {
    return loadServices(clazz, SERVICES_ROOT + clazz.getName(), Thread.currentThread().getContextClassLoader());
  }

  /**
   * Loads services specified by files on the classpath names by 'resource', through the 
   * specified class loader that implement the specified class or interface. Uses 
   * {@link #loadServices(java.net.URL, java.lang.Class, java.lang.ClassLoader)} for each
   * resource file found.
   * @param <T> The type of the services to load
   * @param clazz The class that the services have to implement
   * @param resource The name of the resource on the classpath to check for services
   * @param loader The class loader to use
   * @return The service instances found on the classpath
   * @throws IOException 
   */
  private static <T> Set<T> loadServices(Class<T> clazz, String resource, ClassLoader loader) throws IOException {
    Set<T> services = new HashSet<T>();
    Enumeration<URL> resources = loader.getResources(resource);
    while (resources.hasMoreElements()) {
      services.addAll(loadServices(resources.nextElement(), clazz, loader));
    }
    return services;
  }

  /**
   * Loads services specified in the 'resource', through the 
   * specified class loader that implement the specified class or interface. Found classes are
   * constructed with empty constructor. Individual class loading failures are logged but won't
   * stop the loading process. 
   * @param <T> The type of the services to load
   * @param clazz The class that the services have to implement
   * @param resource The URL to the resource to load services from
   * @param loader The class loader to use
   * @return The service instances found in the resource
   * @throws IOException 
   */
  private static <T> Set<T> loadServices(URL resource, Class<T> clazz, ClassLoader loader) throws IOException {
    Set<T> services = new HashSet<T>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(resource.openStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        T t = loadService(line, clazz, loader);
        if (t != null) {
          services.add(loadService(line, clazz, loader));
        }
      }
    } catch (IOException ioe) {
      logger.error("Could not load services from " + resource + ": " + ioe.getMessage(), ioe);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
    return services;
  }

  @SuppressWarnings("unchecked")
  private static <T> T loadService(String className, Class<T> clazz, ClassLoader loader) {
    T t = null;
    try {
      Class<?> byName = Class.forName(className, true, loader);
      if (clazz.isAssignableFrom(byName)) {
        Class<T> casted = (Class<T>) byName;
        t = casted.newInstance();
      } else {
        logger.error("Service " + className + " does not implement " + clazz.getName() + ", ignoring");
      }
    } catch (ClassNotFoundException cnfe) {
      logger.error("Could not load service " + className + ", class not found", cnfe);
    } catch (InstantiationException ie) {
      logger.error("Could not load service " + className + ", exception during instantiation", ie);
    } catch (IllegalAccessException iae) {
      logger.error("Could not load service " + className + ", illegal access", iae);
    }
    return t;
  }
}
