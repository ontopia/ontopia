// $Id: DefaultCache.java,v 1.3 2007/10/03 11:26:31 geir.gronmo Exp $

package net.ontopia.persistence.proxy;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.utils.StringUtils;

/**
 * INTERNAL: CacheIF implementation that wraps a Map. All access is
 * synchronized.
 */

public final class DefaultCache implements CacheIF {

  protected Map cache;
  
  DefaultCache(Map cache) {
    this.cache = cache;
  }

  public synchronized Object get(Object key) {
    return cache.get(key);
  }

  public synchronized Object put(Object key, Object value) {
    return cache.put(key, value);
  }
  
  public synchronized Object remove(Object key, boolean notifyCluster) {
    return cache.remove(key);
  }
  
  public synchronized void removeAll(Collection keys, boolean notifyCluster) {
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      cache.remove(iter.next());
    }
  }

  public synchronized void clear(boolean notifyCluster) {
    cache.clear();
  }
  
  public void writeReport(java.io.Writer out, boolean dumpCache) throws java.io.IOException {
    synchronized (cache) {
      out.write("<p>Cache size: " + cache.size() + "</p>\n");
      
      if (dumpCache) {
        out.write("<table>\n");
        Iterator iter = cache.keySet().iterator();
        while (iter.hasNext()) {
          Object key = iter.next();
          if (key == null) continue;
          Object val = cache.get(key);
          out.write("<tr><td>");
          out.write((key == null ? "null" : StringUtils.escapeHTMLEntities(key.toString())));
          out.write("</td><td>");
          out.write((val == null ? "null" : StringUtils.escapeHTMLEntities(val.toString())));
          out.write("</td></tr>\n");
        }
        out.write("</table><br>\n");
      }
    }
  }

}
