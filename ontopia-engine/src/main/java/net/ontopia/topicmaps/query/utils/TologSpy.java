
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
    if (is_recording)
      profiler.recordExecute(query.toString(), start, end);
  }
}
