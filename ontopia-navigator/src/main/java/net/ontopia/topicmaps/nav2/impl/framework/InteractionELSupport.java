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

package net.ontopia.topicmaps.nav2.impl.framework;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.PageContext;

import net.ontopia.topicmaps.nav2.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL.
 */
public class InteractionELSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(InteractionELSupport.class.getName());

  /**
   * Breaks name up into tokens separated by '.' characters,
   * looks up the first token (in other JSTL scopes) to get a variable.
   * Uses the next token (if there) to get a bean property from the variable.
   * Uses additional tokens to lookup bean properties of bean properties.
   * If the variable or any of those bean properties is a Map
   * then gets the object with the given token as key.
   * @param name The name to lookup.
   * @param pageContext Used to look up attributes.
   */
  public static Object getValue(String name, PageContext pageContext) {
    log.debug("Looking up name: " + name + " in PageContext.");
    StringTokenizer tok = new StringTokenizer(name);
    if (tok.hasMoreTokens()) {
      String currentName = tok.nextToken(".");
      log.debug("firstName " + currentName);
      Object retVal = pageContext.findAttribute(currentName);
      
      while (tok.hasMoreTokens()) {
        currentName = tok.nextToken(".");
        log.debug("currentName " + currentName);
        retVal = getBeanProperty(retVal, currentName);
        log.debug("current object: " + retVal);
      }
      return retVal;
    }
    
    return null;
  }
  
  private static String giveLeadingCapital(String source) {
    return source.substring(0, 1).toUpperCase() + source.substring(1);
  }
  
  private static Object getBeanProperty(Object object, String name) {
    if (object instanceof Map) {
      Map map = (Map)object;
      log.debug("getting map value with name: " + name + " from map " + map);
      return map.get(name);
    }
    
    if (object == null) {
      return null;
    }
    
    try {
      name = giveLeadingCapital(name);
      log.debug("Looking up bean property: " + name + " of " + object);
      Method method = object.getClass().getMethod("get" + name, new Class[]{});
      log.debug("Attempting to invoke method:" + method + " of " + object);
      return method.invoke(object, new Object[]{});
    } catch (NoSuchMethodException e) {
      log.debug("getBeanProperty NoSuchMethodException; class: " 
          + object.getClass());
      return null;
    } catch (java.lang.reflect.InvocationTargetException e) {
      log.debug("getBeanProperty InvocationTargetException");
      return null;
    } catch (IllegalAccessException e) {
      log.debug("getBeanProperty IllegalAccessException");
      return null;
    }
  }

  /**
   * Looks up 'name' in the oks scope.
   * If not found, looks up tokenized 'name' in other scopes using the
   * local getValue method (see details of getValue().
   */
  public static Collection extendedGetValue (String name, 
                                             PageContext pageContext) {
    Collection retVal = ContextUtils.getValue(name, pageContext, null);
    if (retVal == null) {
      log.debug("Couldn't find variable '" + name + "' in oks scope."
          + " Trying other scopes.");
      Object val = getValue(name, pageContext);
      log.debug("Value of val is: " + val + ". class is: " 
          + (val == null ? "null." : val.getClass().getName()));
      if (val == null) {
        retVal = Collections.EMPTY_SET;
        log.debug("extendedGetValue returning an empty set.");
      } else if (val instanceof Collection) {
        retVal = (Collection)val;
        log.debug("extendedGetValue returning a collection.");
      } else {
        retVal = Collections.singleton(val);
        log.debug("extendedGetValue returning object wrapped in a collection.");
      }
    }
    return retVal;
  }
  
  /**
   * Takes a name value of "true", "false", or a variable name and
   * returns a boolean representing the value given. If the name value
   * is a variable name the method will return true if the variable
   * value is not empty.
   * 
   * @param name The value to be evaluated.
   * @param pageContext The current page execution context.
   * @return a boolean indicating whether or not the value is true or false.
   */
  public static boolean getBooleanValue(String name, boolean defaultValue,
                                        PageContext pageContext) {
    if (name == null) {
      return defaultValue;
    } else if ("true".equals(name) || "yes".equals(name)) {
      return true;
    } else if ("false".equals(name) || "no".equals(name)) {
      return false;
    } else {
      Collection value = extendedGetValue(name, pageContext);
      if (value.isEmpty()) {
        return false;
      }
      
      // get first element in collection
      Object first;
      if (value instanceof List) {
        first = ((List)value).get(0);
      } else {
        first = value.iterator().next();
      }
        
      // true if first element is not null and not boolean false
      if (first == null) {
        return false;
      } else {
        if (first instanceof Boolean) {
          return ((Boolean)first).booleanValue();
        } else {
          return true;
        }
      }
    }
  }
  
}
