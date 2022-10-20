/*
 * #!
 * Ontopia Vizigator
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
package net.ontopia.topicmaps.viz;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VizDebugUtils {
  
  public static TimerManager man = new TimerManager();
  private static boolean instrumentedDebugOn = false;
  private static long startTime = System.currentTimeMillis();
  
  // NOTE: Don't turn on debugging at runtime. Can cause NullPointerException
  private static boolean isDebugEnabled = false;
  private static boolean isTimeDebugEnabled = false;
  private static boolean isAnimatorEnabled = false;
  private static boolean isNeighbouringCircleEnabled = true;
  protected static final boolean ENABLE_MOTION_CONFIGURATION = true;


  // Made isDebugFailMode final false since not using it for the time being.
  private static final boolean isDebugFailMode = false;

  private static PrintStream out = System.out;

  public static boolean isDebugEnabled() {
    return isDebugEnabled;
  }
  
  public static boolean isAnimatorEnabled() {
    return isAnimatorEnabled;
  }
  
  public static boolean isNeighbouringCircleEnabled() {
    return isNeighbouringCircleEnabled;
  }

  public static boolean isDebugFailMode() {
    return isDebugFailMode;
  }

  public static void setDebugFailMode(boolean fail) {
    // Made isDebugFailMode final false.
    // isDebugFailMode = fail;
  }

  public static void debug(String source) {
    if (isDebugEnabled) {
      out.println(source);
    }
  }
  
  public static void timeDebug(String label) {
    if (isTimeDebugEnabled) {
      out.println(getTimeDelta() + " - " + label);
    }
  }
  
  public static void resetTimer() {
    startTime = System.currentTimeMillis();
  }
  
  public static String getTimeDelta() {
    return formatTimeDeltaValue(getTimeDeltaValue());
  }
  
  private static String pad(long toPad, int length) {
    String retVal = Long.toString(toPad);
    
    while (retVal.length() < length) {
      retVal = "0" + retVal;
    }
    
    return retVal;
  }
  
  public static String formatTimeDeltaValue(long delta) {
    if (delta >= 1000) {
      return "" + (delta / 1000) + "." + pad(delta % 1000, 3) + " seconds";
    }
    if (delta >= 100) {
      return "0." + delta + " seconds";
    }
    if (delta >= 10) {
      return "0.0" + delta + " seconds";
    }
    if (delta >= 0) {
      return "0.00" + delta + "seconds";
    }
    return "ERROR: Negative time!";
  }
  
  public static long getTimeDeltaValue() {
    return System.currentTimeMillis() - startTime;
  }
  
  public static void instrumentedDebug(String source) {
    if (instrumentedDebugOn) {
      out.println(source);
    }
  }

  public static class TimerManager {
    private Map codeTimers;
    
    public TimerManager() {
      codeTimers = new HashMap();
    }
    
    public void recreate(String id) {
      if (!isDebugEnabled) {
        return;
      }
      
      codeTimers.put(id, new CodeTimer(id));
    }
    
    public CodeTimer getOrCreate(String id) {
      if (!isDebugEnabled) {
        return null;
      }
      
      CodeTimer timer = (CodeTimer)codeTimers.get(id);
      if (timer == null) {
        timer = new CodeTimer(id);
        codeTimers.put(id, timer);
      }
      return timer;
    }
    
    public void start(String id) {
      if (!isDebugEnabled) {
        return;
      }
      
      CodeTimer timer = getOrCreate(id);
      timer.start();
    }

    public void stop(String id) {
      if (!isDebugEnabled) {
        return;
      }
      
      CodeTimer timer = getOrCreate(id);
      timer.stop();
    }

    public void report(String id) {
      if (!isDebugEnabled) {
        return;
      }
      
      CodeTimer timer = getOrCreate(id);
      timer.report();
    }
    
    public void reportAll(String subtitle) {
      if (!isDebugEnabled) {
        return;
      }
      
      Iterator timersIt = codeTimers.values().iterator();
      while (timersIt.hasNext()) {
        CodeTimer timer = (CodeTimer)timersIt.next();
        timer.report(subtitle);
      }
    }
    
    public void reportAll() {
      if (!isDebugEnabled) {
        return;
      }
      
      reportAll(null);
    }

    public void report(String id, String subtitle) {
      if (!isDebugEnabled) {
        return;
      }
      
      CodeTimer timer = (CodeTimer)codeTimers.get(id);
      timer.report();
    }
  }
  
  public static class CodeTimer {
    private String title;
    private boolean started;
    private long startTime;
    private int stopCount;
    private long totalTime;
    
    public CodeTimer(String title) {
      if (!isDebugEnabled) {
        return;
      }
      
      this.title = title;
    }
    
    public void start() {
      if (!isDebugEnabled) {
        return;
      }
      
      startTime = System.currentTimeMillis();
      started = true;
    }
    
    public void stop() {
      if (!isDebugEnabled) {
        return;
      }
      
      if (!started) {
        return;
      }
      
      totalTime += System.currentTimeMillis() - startTime;
      stopCount++;
    }
    
    public void report() {
      if (!isDebugEnabled) {
        return;
      }
      
      report(null);
    }

    public void report(String subtitle) {
      if (!isDebugEnabled) {
        return;
      }
      
      String titleString = title + (subtitle == null ? "" : ":" + subtitle);
      out.println("CodeTimer(" + titleString
          + ") - Total time: " + formatTimeDeltaValue(totalTime)
          + ", Count: " + formatTimeDeltaValue(stopCount)
          + ", Mean: " + formatTimeDeltaValue(divide(totalTime, stopCount)));
    }
    
    private long divide(long dividend, long divisor) {
      if (divisor == 0l) {
        return 0l;
      }
      return dividend / divisor;
    }
  }
}
