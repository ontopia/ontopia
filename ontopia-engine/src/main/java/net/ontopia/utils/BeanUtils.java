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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * PUBLIC: Utilities for treating objects as beans.
 * @since 5.4.0
 */
public class BeanUtils {

  private BeanUtils() {
  }

  /**
   * Maps all get* methods results to a map.
   * Replacement for BeanMap to avoid big dependency.
   * @since 5.4.0
   */
  public static Map<String, String> beanMap(Object bean, boolean sorted) {
    Map<String, String> map =
            sorted ? new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
                   : new HashMap<String, String>();

    for (Method method : bean.getClass().getMethods()) {
      if (method.getName().startsWith("get") && !method.getName().equals("getClass") && (method.getReturnType() != null)) {
        try {
          Object o = method.invoke(bean);
          String name = method.getName().substring(3);
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
          map.put(name, o == null ? "null" : o.toString());
        } catch (Exception e) {
          // could not get property: ignore
        }
      }
    }
    return map;
  }
}
