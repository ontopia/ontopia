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

/**
 * INTERNAL: Class that contains useful methods.
 */
public class ObjectUtils {

  private ObjectUtils() {
  }

  /**
   * INTERNAL: Create new instance of given class. Class must have a
   * default constructor.
   */
  public static Object newInstance(String className) {    
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class klass = Class.forName(className, true, classLoader);
      return klass.newInstance();
    }
    catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
