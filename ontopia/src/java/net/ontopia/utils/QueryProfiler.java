
// $Id: QueryProfiler.java,v 1.1 2008/12/04 11:32:59 lars.garshol Exp $

package net.ontopia.utils;

import java.io.Writer;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.text.DecimalFormat;
import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: Statistics collector for profiling queries, whether tolog
 * or SQL.
 */
public class QueryProfiler {
  static DecimalFormat df = new DecimalFormat("0.#");
  Map eStats = new HashMap();

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
  }

  // -- Utilities

  private Event getEvent(String ename) {
    Event event = (Event) eStats.get(ename);
    if (event == null) {
      event = new Event(ename);
      eStats.put(ename, event);
    }
    return event;
  }

  // -- report generation

  public synchronized void generateReport(String title, Writer out)
    throws IOException {
    out.write("<h1>" + title + "</h1>\n");

    out.write("<table>\n");
    out.write("<tr><th> <th>Query <th colspan=\"2\">Total time <th>Avg <th>Min <th>Max <th>Traverse time");
    out.write("<th>Times run");
    out.write("<th>Rows\n");

    Object[] events = eStats.values().toArray();
    Arrays.sort(events, new EventComparator());

    // calculate total time
    long executeTime = 0;
    long executeNum = 0;
    long traverseTime = 0;
    long traverseNum = 0;
    for (int i=0; i < events.length; i++) {
      Event event = (Event)events[i];
      executeTime += event.executeTime;
      executeNum += event.executeNum;
      traverseTime += event.traverseTime;
      traverseNum += event.traverseNum;
    }

    // output data      
    for (int i=0; i < events.length; i++) {
      Event event = (Event)events[i];

      out.write("<tr><td>");
      out.write(Integer.toString(i+1));
      out.write(". <td class=\"event\">");
      out.write(event.toString());
      out.write("<td>");
      out.write(Long.toString(event.executeTime));
      out.write("<td>");
      out.write(df.format((1.0f*event.executeTime / executeTime) * 100));
      out.write("% <td>");
      out.write(df.format((1.0f*event.executeTime)/event.executeNum));
      out.write("<td>");
      out.write(df.format(event.executeTimeMin));
      out.write("<td>");
      out.write(df.format(event.executeTimeMax));
      out.write("<td>");
      out.write(Long.toString(event.traverseTime));
      out.write("<td>");
      out.write(Long.toString(event.executeNum));
      out.write("<td>");
      out.write(Long.toString(event.traverseNum));
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
    String name;

    long executeNum;
    long executeTime;
    float executeTimeMin = -1.0f;
    float executeTimeMax = -1.0f;

    long traverseTime;
    long traverseNum;

    Event(String name) {
      this.name = name;
    }

    void addExecuteUpdate(long startTime, long endTime, int affectedSize) {
      addExecute(startTime, endTime);
      traverseNum += affectedSize;
    }

    void addExecute(long startTime, long endTime) {
      int time = (int)(endTime - startTime);
      if (executeTimeMax == -1.0f || time > executeTimeMax)
        executeTimeMax = (float)time;
      if (executeTimeMin == -1.0f || time < executeTimeMin)
        executeTimeMin = (float)time;

      executeTime = executeTime + time;
      executeNum++;
    }

    void addExecute(long startTime, long endTime, int executeCount) {
      int time = (int)(endTime - startTime);
      float timeAvg = (time/(executeCount*1.0f));
      if (executeTimeMax == -1.0f || timeAvg > executeTimeMax)
        executeTimeMax = timeAvg;
      if (executeTimeMin == -1.0f || timeAvg < executeTimeMin)
        executeTimeMin = timeAvg;

      executeTime = executeTime + time;
      executeNum += executeCount;
    }

    void addTraverse(boolean hasNext, long startTime, long endTime) {
      int time = (int)(endTime - startTime);
      executeTime = executeTime + time;
      traverseTime = traverseTime + time;
      if (hasNext) traverseNum++;
    }

    public String toString() {
      if (name == null)
        return null;
      else
        return StringUtils.escapeHTMLEntities(name);
    }
  }

  static class EventComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      Event e1 = (Event)o1;
      Event e2 = (Event)o2;
      if (e1 == null) return -1;
      if (e2 == null) return 1;
      long e1time = e1.executeTime;
      long e2time = e2.executeTime;
      return (e1time > e2time ? -1 : (e1time < e2time ? 1 : 0));
    }
    
  }
}
