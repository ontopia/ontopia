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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Helper class for providing some more convenience for time
 * measurement. Based on system clock in milliseconds.
 */
public class TimeMeasureUtils {
  static Logger logger = LoggerFactory.getLogger(TimeMeasureUtils.class.getName());
  private static Map timeTable = new HashMap();

  public static void startAction(String key) {
    timeTable.put(key + "-start", new Long(System.currentTimeMillis()));
  }

  public static void endAction(String key) {
    long endtime = System.currentTimeMillis();
    long time = endtime - ((Long) timeTable.get(key + "-start")).longValue();

    Long elapsed = (Long) timeTable.get(key);
    if (elapsed == null)
      elapsed = new Long(time);
    else
      elapsed = new Long(time + elapsed.longValue());

    timeTable.put(key, elapsed);
  }

  public static void report(Writer out) throws java.io.IOException {
    out.write("--------------------------------------------------\n");
    Iterator it = timeTable.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      if (!key.endsWith("-start"))
        out.write("" + key + "\t" + timeTable.get(key) + "\n");
    }
    out.write("--------------------------------------------------\n");
  }

  // returns time in seconds
  public static double getTimeInSeconds(String key) {
    return ((Long) timeTable.get(key)).longValue() / 1000.0;
  }
  
  public static void reset() {
    timeTable = new HashMap();
  }
}
