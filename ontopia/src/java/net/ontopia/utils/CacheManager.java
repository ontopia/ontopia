// $Id: CacheManager.java,v 1.12 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A manager class that manages cached objects. Cached objects
 * can be registered with the cache manager in order to follow the
 * life cycle set by the environment using the cache manager. The
 * cached objects must implement the CachedIF interface.</p>
 *
 * Note that this class also implements the CachedIF interface, so
 * that it can itself be fully refreshed and also be managed by other
 * cache managers.</p>
 */

public class CacheManager implements CachedIF {

  protected Map caches = new HashMap();

  public Set getGroups() {
    return caches.keySet();
  }

  public Set getCached(Object group) {
    return (Set)caches.get(group);
  }
  
  public void addCached(CachedIF cached, Object group) {
    if (!caches.containsKey(group)) caches.put(group, new HashSet());
    ((Set)caches.get(group)).add(cached);
  }

  public void removeCached(CachedIF cached, Object group) {
    Set grouped = (Set)caches.get(group);
    grouped.remove(group);
    if (grouped.isEmpty())
      caches.remove(group);
  }

  public void refresh(Object group) {
    if (!caches.containsKey(group)) return;
    Iterator iter = ((Set)caches.get(group)).iterator();
    while (iter.hasNext()) {
      CachedIF cached = (CachedIF)iter.next();
      cached.refresh();
    }
  }

  public void refresh() {
    Iterator iter1 = caches.keySet().iterator();
    while (iter1.hasNext()) {
      Object group = iter1.next();
      Iterator iter2 = ((Set)caches.get(group)).iterator();
      while (iter2.hasNext()) {
        CachedIF cached = (CachedIF)iter2.next();
        cached.refresh();
      }
    }
  }
  
}




