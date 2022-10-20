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

package net.ontopia.topicmaps.query.utils;

import java.io.Writer;
import java.io.IOException;
import net.ontopia.utils.QueryProfiler;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * PUBLIC: Query profiler for tolog. Note that it must be turned on by
 * a call to setIsRecording() in order to start recording data.
 *
 * @since 5.0.0
 */
public class TologSpy {
  private static boolean is_recording = false;
  private static QueryProfiler profiler = new QueryProfiler();

  /**
   * PUBLIC: Returns true if the profiler is recording query data.
   */
  public static boolean getIsRecording() {
    return is_recording;
  }
  
  /**
   * PUBLIC: Used to turn recording on and off.
   */
  public static void setIsRecording(boolean recording) {
    is_recording = recording;
  }

  /**
   * PUBLIC: Clears recorded data.
   */
  public static void clear() {
    profiler.clear();
  }

  /**
   * PUBLIC: Produce profiling report.
   */
  public static void generateReport(Writer out) throws IOException {
    profiler.generateReport("tologSpy results", out);
  }
  
  /**
   * INTERNAL: Records data about a query.
   */
  public static void recordExecute(TologQuery query, long start, long end) {
    if (is_recording) {
      profiler.recordExecute(query.toString(), start, end);
    }
  }
}
