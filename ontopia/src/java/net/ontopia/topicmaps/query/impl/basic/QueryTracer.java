
// $Id: QueryTracer.java,v 1.22 2009/05/18 12:26:12 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.basic;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.ontopia.topicmaps.query.impl.utils.QueryTraceListenerIF;
import net.ontopia.topicmaps.query.parser.AbstractClause;
import net.ontopia.topicmaps.query.parser.NotClause;
import net.ontopia.topicmaps.query.parser.OrClause;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Used for testing and timing of queries.
 */
public class QueryTracer {
  // --- initialize logging facility.
  static Logger logger = Logger.getLogger(QueryTracer.class.getName());
  
  private static List listeners = new java.util.ArrayList();

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
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.startQuery();
    }
  }

  public static void endQuery() {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.endQuery();
    }
  }
  
  public static void trace(BasicPredicateIF predicate, List arguments) {
  }

  public static void trace(String msg) {
    write(msg + "\n");
  }

  public static void trace(String msg, int[] array) {
    write(msg + ": " + net.ontopia.utils.DebugUtils.toString(array) + "\n");
  }
  
  public static void trace(String msg, Object[] array) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.trace(msg + ": " + net.ontopia.utils.DebugUtils.toString(array) + "\n");
    }
  }
  
  public static void trace(String msg, QueryMatches matches) {
    write("\n\n" + msg + "\n");
    write(matches.dump() + "\n");
  }

  public static void atTime(String msg) {
    write("(" + System.currentTimeMillis() + ") " + msg + "\n");
  }

  public static void enter(BasicPredicateIF predicate,
                           AbstractClause clause,
                           QueryMatches input) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.enter(predicate, clause, input);
    }
  }

  public static void enter(OrClause clause, QueryMatches input) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.enter(clause, input);
    }
  }
  
  public static void enter(List branch) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.enter(branch);
    }
  }
  
  public static void leave(QueryMatches result) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.leave(result);
    }
  }

  public static void leave(List branch) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.leave(branch);
    }
  }

  public static void enterOrderBy() {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.enterOrderBy();
    }
  }

  public static void leaveOrderBy() {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.leaveOrderBy();
    }
  }
  
  public static void enterSelect(QueryMatches result) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.enterSelect(result);
    }
  }
  
  public static void leaveSelect(QueryMatches result) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      QueryTraceListenerIF listener = (QueryTraceListenerIF) it.next();
      listener.leaveSelect(result);
    }
  }
  
  // --- Internal methods

  private static void write(String string) {
//     System.out.println(string);
    
//     try {
//       java.io.FileWriter writer = new java.io.FileWriter("/tmp/debug.txt", true);
//       writer.write(string + "\n");
//       writer.close();
//     } catch (IOException e) {
//       e.printStackTrace();
//     }
  }

  // --- QueryTraceListenerIF

  public static class TracePrinter implements QueryTraceListenerIF {

    public static long memoryDeltaThreshold = 1024*1024*5;
    public static float elapsedThreshold = 3.0f;

    private ThreadLocal ti = new ThreadLocal();

    static class Info {
      private Stack entered = new Stack(); // has times for predicates we've entered + entire query
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
        Info info = (Info)ti.get();

        Runtime rt = Runtime.getRuntime();
        memoryDelta = (info.freeMemory - rt.freeMemory())  - (rt.totalMemory() - info.totalMemory);
        //! output(getIndent(info) + "MEMORY (" + memoryDelta + ")");

        elapsed = getElapsed(info);
        output("</query " + elapsed + ", m: " + memoryDelta + ">");
        info.entered.pop();
      }

      // log memory consuming query
      if (memoryDelta > memoryDeltaThreshold)
        logger.warn("Query execution exceeded memory delta threshold " + memoryDelta + " bytes", new RuntimeException());

      // log time consuming query
      if (elapsed > elapsedThreshold)
        logger.warn("Query execution exceeded time elapsed threshold " + elapsed + " seconds", new RuntimeException());      
    }
    
    public void enter(BasicPredicateIF predicate, AbstractClause clause, 
                      QueryMatches input) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "ENTER (" + (input.last + 1) + "): " + clauseToString(clause));
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leave(QueryMatches result) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "LEAVE (" + getElapsed(info) + ", " + (result.last + 1) + ")");
        info.entered.pop();
      }
    }
    
    private String clauseToString(AbstractClause clause) {
      if (clause instanceof NotClause)
        return "not( ... )";
      if (clause instanceof OrClause) {
				OrClause orClause = ((OrClause)clause);
        int size = orClause.getAlternatives().size();
				if (orClause.getShortCircuit())
					return "{ ... SHORTCIRCUITING OR, " + size + " alternatives ... }";
        else if (size == 1)
          return "{ ... OPTIONAL ... }";
				else
					return "{ ... OR, " + size + " alternatives ... }";
      }
      return clause.toString();
    }
    
    public void enter(OrClause clause, QueryMatches input) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "ENTER (" + (input.last + 1) + "): " + clauseToString(clause));
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void enter(List branch) {
      if (!isEnabled())
        return;
    }

    public void leave(List branch) {
      if (!isEnabled())
        return;
    }

    public void enterOrderBy() {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "ENTER order by");
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leaveOrderBy() {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "LEAVE (" + getElapsed(info) + ")");
        info.entered.pop();
      }
    }

    public void enterSelect(QueryMatches result) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "ENTER select (" + (result.last + 1) + ")");
        info.entered.push(new Long(System.currentTimeMillis()));
      }
    }

    public void leaveSelect(QueryMatches result) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
        output(getIndent(info) + "LEAVE select (" + getElapsed(info) + ", " + (result.last + 1) + ")");
        info.entered.pop();
      }
    }

    public void trace(String message) {
      if (isEnabled()) {
        Info info = (Info)ti.get();
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
