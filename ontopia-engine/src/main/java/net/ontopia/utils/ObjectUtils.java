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

  /**
   * INTERNAL: Extract real exception from wrapper exception.
   */
  public static Throwable getRealCause(Throwable t) {
    Throwable cause;
    if (t instanceof OntopiaRuntimeException)
      cause = ((OntopiaRuntimeException)t).getCause();
    else if (t instanceof org.xml.sax.SAXException)
      cause = ((org.xml.sax.SAXException)t).getException();
    else
      cause = null; // may want to support 1.4 getCause method
    
    if (cause != null)
      return getRealCause(cause);
    else
      return t;
  }

  /**
   * INTERNAL: Extract real exception from wrapper exception and
   * rethrow as a RuntimeException.
   */
  public static void throwRuntimeException(Throwable t) {
    Throwable x = getRealCause(t);
    if (x instanceof RuntimeException)
      throw ((RuntimeException)x);
    else
      throw new OntopiaRuntimeException(x);
  }
  
}
