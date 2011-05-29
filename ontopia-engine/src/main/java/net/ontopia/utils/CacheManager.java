
package net.ontopia.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

  protected Map<Object, Set<CachedIF>> caches = new HashMap<Object, Set<CachedIF>>();

  public Set<Object> getGroups() {
    return caches.keySet();
  }

  public Set<CachedIF> getCached(Object group) {
    return caches.get(group);
  }
  
  public void addCached(CachedIF cached, Object group) {
    if (!caches.containsKey(group)) caches.put(group, new HashSet<CachedIF>());
    caches.get(group).add(cached);
  }

  public void removeCached(CachedIF cached, Object group) {
    Set<CachedIF> grouped = caches.get(group);
    // todo: correct fix?
	grouped.remove(cached);
    if (grouped.isEmpty())
      caches.remove(group);
  }

  public void refresh(Object group) {
    if (!caches.containsKey(group)) return;
    Iterator<CachedIF> iter = caches.get(group).iterator();
    while (iter.hasNext()) {
      CachedIF cached = iter.next();
      cached.refresh();
    }
  }

  public void refresh() {
    Iterator<Object> iter1 = caches.keySet().iterator();
    while (iter1.hasNext()) {
      Object group = iter1.next();
      Iterator<CachedIF> iter2 = caches.get(group).iterator();
      while (iter2.hasNext()) {
        CachedIF cached = iter2.next();
        cached.refresh();
      }
    }
  }
  
}




