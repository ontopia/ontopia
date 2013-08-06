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

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Useful tracing methods. This class can be used to
 * validate calls that enter and leave methods. The specified method
 * name plus the current thread is used as key. A counter is
 * maintained for methods that have been entered.
 */
public class TraceUtils {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(TraceUtils.class.getName());

  private static Map syncs = new HashMap();

  public static Map getSyncMap() {
    return syncs;
  }
  
  public static synchronized void enter(String method) {
    // Create key
    String key = Thread.currentThread() + " " + method;
    // Increment counter
    Integer val = (Integer)syncs.get(key);
    if (val == null) val = new Integer(0);
    syncs.put(key, new Integer(val.intValue() + 1));
  }

  public static synchronized void leave(String method) {
    // Create key
    String key = Thread.currentThread() + " "  + method;
    // Decrement counter and/or drop entry
    Integer val = (Integer)syncs.get(key);
    if (val == null) {
      log.debug("Cannot leave '" + key + "' when not entered.");
    } else {
      int newval = val.intValue() - 1;
      if (newval == 0)
        syncs.remove(key);
      else
        syncs.put(key, new Integer(newval));
    }
  }
  
}
