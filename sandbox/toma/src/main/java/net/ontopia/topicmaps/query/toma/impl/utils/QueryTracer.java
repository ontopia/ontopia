/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.utils;

import java.util.List;
import java.util.Stack;

import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Used for testing and timing of queries.
 */
public class QueryTracer {
  // --- initialize logging facility.
  static Logger logger = LoggerFactory.getLogger(QueryTracer.class.getName());

  private static List<QueryTraceListenerIF> listeners = new java.util.ArrayList<QueryTraceListenerIF>();

  static {
    addListener(new TracePrinter());
  }

  public static void addListener(QueryTraceListenerIF listener) {
    listeners.add(listener);
  }

  public static void removeListener(QueryTraceListenerIF listener) {
    listeners.remove(listener);
  }

  // --- Tracer methods

  public static void startQuery() {
    for (QueryTraceListenerIF listener : listeners) {
      listener.startQuery();
    }
  }

  public static void endQuery() {
    for (QueryTraceListenerIF listener : listeners) {
      listener.endQuery();
    }
  }

  public static void enter(ExpressionIF expr) {
    for (QueryTraceListenerIF listener : listeners) {
      listener.enter(expr);
    }
  }

  public static void leave(ResultSet result) {
    for (QueryTraceListenerIF listener : listeners) {
      listener.leave(result);
    }
  }

  public static void enterOrderBy() {
    for (QueryTraceListenerIF listener : listeners) {
      listener.enterOrderBy();
    }
  }

  public static void leaveOrderBy() {
    for (QueryTraceListenerIF listener : listeners) {
      listener.leaveOrderBy();
    }
  }

  public static void enterSelect(ResultSet result) {
    for (QueryTraceListenerIF listener : listeners) {
      listener.enterSelect(result);
    }
  }

  public static void leaveSelect(ResultSet result) {
    for (QueryTraceListenerIF listener : listeners) {
      listener.leaveSelect(result);
    }
  }

  // --- QueryTraceListenerIF

  public static class TracePrinter implements QueryTraceListenerIF {

    public static long memoryDeltaThreshold = 1024 * 1024 * 5;
    public static float elapsedThreshold = 3.0f;

    private ThreadLocal<Info> ti = new ThreadLocal<Info>();

    static class Info {
      // has times for predicates we've entered + entire query
      private Stack<Long> entered = new Stack<Long>();
      private long totalMemory;
      private long freeMemory;
    }

    public boolean isEnabled() {
      return logger.isDebugEnabled();
    }

    public void output(String message) {
      logger.debug(message);
    }

    public void startQuery() {
      if (isEnabled()) {
        Info info = new Info();
        ti.set(info);

        output("<query>");
        info.entered.push(new Long(System.currentTimeMillis()));

        Runtime rt = Runtime.getRuntime();
        info.totalMemory = rt.totalMemory();
        info.freeMemory = rt.freeMemory();
      }
    }

    public void endQuery() {
      long memoryDelta = 0l;
      float elapsed = 0f;
      if (isEnabled()) {
        Info info = (Info) ti.get();

        Runtime rt = Runtime.getRuntime();
        memoryDelta = (info.freeMemory - rt.freeMemory())
            - (rt.totalMemory() - info.totalMemory);
        // ! output(getIndent(info) + "MEMORY (" + memoryDelta + ")");

        elapsed = getElapsed(info);
        output("</query " + elapsed + ", m: " + memoryDelta + ">");
        info.entered.pop();
      }

      // log memory consuming query
      if (memoryDelta > memoryDeltaThreshold)
        logger.warn("Query execution exceeded memory delta threshold "
            + memoryDelta + " bytes", new RuntimeException());

      // log time consuming query
      if (elapsed > elapsedThreshold)
        logger.warn("Query execution exceeded time elapsed threshold "
            + elapsed + " seconds", new RuntimeException());
    }

    public void enter(ExpressionIF expr) {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "ENTER (" + 0 + "): " + expr.toString());
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leave(ResultSet result) {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "LEAVE (" + getElapsed(info) + ", "
            + result.getRowCount() + ")");
        info.entered.pop();
      }
    }

    public void enterOrderBy() {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "ENTER order by");
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leaveOrderBy() {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "LEAVE (" + getElapsed(info) + ")");
        info.entered.pop();
      }
    }

    public void enterSelect(ResultSet result) {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "ENTER select (" + 0 + ")");
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leaveSelect(ResultSet result) {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + "LEAVE select (" + getElapsed(info) + ", " + 0
            + ")");
        info.entered.pop();
      }
    }

    public void trace(String message) {
      if (isEnabled()) {
        Info info = (Info) ti.get();
        output(getIndent(info) + message);
      }
    }

    // internal helpers

    private String getIndent(Info info) {
      StringBuffer buf = new StringBuffer();
      for (int ix = 0; ix < info.entered.size(); ix++)
        buf.append("  ");
      return buf.toString();
    }

    private float getElapsed(Info info) {
      try {
        Long start = (Long) info.entered.peek();
        return (float) (System.currentTimeMillis() - start.longValue()) / 1000;
      } catch (java.util.EmptyStackException e) {
        return -1000;
      }
    }
  }
}
