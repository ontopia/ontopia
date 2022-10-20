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

import java.io.Writer;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.text.DecimalFormat;

/**
 * INTERNAL: Statistics collector for profiling queries, whether tolog
 * or SQL.
 */
public class QueryProfiler {
  private static DecimalFormat df = new DecimalFormat("0.#");
  private Map<String, Event> eStats;
  /**
   * Tracks whether traversal events have been seen. The tolog profiler does
   * not provide these, and tracking this lets us leave out two unnecessary
   * columns in the report.
   */
  private boolean traverse;

  public QueryProfiler() {
    eStats = new HashMap<String, Event>();
    traverse = false;
  }

  public synchronized void clear() {
    eStats.clear();
  }
  
  public synchronized void recordExecute(String ename, long start, long end) {
    Event event = getEvent(ename);
    event.addExecute(start, end);
  }

  public synchronized void recordExecuteUpdate(String ename,
                                               int affectedSize,
                                               long start, long end) {
    Event event = getEvent(ename);
    event.addExecuteUpdate(start, end, affectedSize);
  }

  public synchronized void recordTraverse(String ename, boolean hasNext,
                                          long start, long end) {
    Event event = getEvent(ename);
    event.addTraverse(hasNext, start, end);
    traverse = true;
  }

  // -- Utilities

  private Event getEvent(String ename) {
    Event event = eStats.get(ename);
    if (event == null) {
      event = new Event(ename);
      eStats.put(ename, event);
    }
    return event;
  }

  // -- report generation

  public synchronized void generateReport(String title, Writer out)
    throws IOException {
    final String TD = "<td>";
    out.write("<h1>" + title + "</h1>\n");

    out.write("<table>\n");
    out.write("<tr><th> <th>Query <th colspan=\"2\">Total time <th>Avg <th>Min <th>Max");
    out.write("<th>Times run\n");
    if (traverse) {
      out.write("<th>Traverse time <th>Rows\n");
    }

    Object[] events = eStats.values().toArray();
    Arrays.sort(events, new EventComparator());

    // calculate total time
    long executeTime = 0;
    long executeNum = 0;
    long traverseTime = 0;
    for (int i=0; i < events.length; i++) {
      Event event = (Event)events[i];
      executeTime += event.executeTime;
      executeNum += event.executeNum;
      traverseTime += event.traverseTime;
    }

    // output data      
    for (int i=0; i < events.length; i++) {
      Event event = (Event)events[i];

      out.write("<tr>" + TD);
      out.write(Integer.toString(i+1));
      out.write(". <td class=\"event\">");
      out.write(event.toString());
      out.write(TD);
      out.write(Long.toString(event.executeTime));
      out.write(TD);
      out.write(df.format((1.0f*event.executeTime / executeTime) * 100));
      out.write("% " + TD);
      out.write(df.format((1.0f*event.executeTime)/event.executeNum));
      out.write(TD);
      out.write(df.format(event.executeTimeMin));
      out.write(TD);
      out.write(df.format(event.executeTimeMax));
      out.write(TD);
      out.write(Long.toString(event.executeNum));
      if (traverse) {
        out.write(TD);
        out.write(Long.toString(event.traverseTime));
        out.write(TD);
        out.write(Long.toString(event.traverseNum));
      }
      out.write("</tr>\n");
    }
    out.write("</table>\n");
    out.write("<p><b>Statements</b>: ");
    out.write(Long.toString(executeNum));
    out.write(", <b>Total time</b>: ");
    out.write(Long.toString(executeTime));
    out.write(" ms, <b>Total traverse time</b>: ");
    out.write(Long.toString(traverseTime));
    out.write(" ms, <b>Average</b>: ");
    out.write(df.format((1.0f*executeTime)/executeNum));
    out.write(" ms</p>\n");    

    out.write("<p>Report generated: ");
    out.write(new java.util.Date().toString());
    out.write(", ");
    out.write(net.ontopia.Ontopia.getInfo());
    out.write("</p>\n");
  }
  
  // --- Event 

  static class Event {
    private String name;

    private long executeNum;
    private long executeTime;
    private float executeTimeMin = -1.0f;
    private float executeTimeMax = -1.0f;

    private long traverseTime;
    private long traverseNum;

    Event(String name) {
      this.name = name;
    }

    private void addExecuteUpdate(long startTime, long endTime, int affectedSize) {
      addExecute(startTime, endTime);
      traverseNum += affectedSize;
    }

    private void addExecute(long startTime, long endTime) {
      int time = (int)(endTime - startTime);
      if (executeTimeMax == -1.0f || time > executeTimeMax) {
        executeTimeMax = (float)time;
      }
      if (executeTimeMin == -1.0f || time < executeTimeMin) {
        executeTimeMin = (float)time;
      }

      executeTime = executeTime + time;
      executeNum++;
    }

    private void addTraverse(boolean hasNext, long startTime, long endTime) {
      int time = (int)(endTime - startTime);
      executeTime = executeTime + time;
      traverseTime = traverseTime + time;
      if (hasNext) {
        traverseNum++;
      }
    }

    @Override
    public String toString() {
      if (name == null) {
        return null;
      } else {
        return StringUtils.escapeHTMLEntities(name);
      }
    }
  }

  static class EventComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
      Event e1 = (Event)o1;
      Event e2 = (Event)o2;
      if (e1 == null) {
        return -1;
      }
      if (e2 == null) {
        return 1;
      }
      long e1time = e1.executeTime;
      long e2time = e2.executeTime;
      return (e1time > e2time ? -1 : (e1time < e2time ? 1 : 0));
    }
    
  }
}
