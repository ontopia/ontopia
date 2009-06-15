// $Id: CachedGrabber.java,v 1.9 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Grabber that maintains a cache of previously grabbed
 * objects. It works with any implementation of GrabberIF.</p>
 * 
 * The cache is first checked to see if a grabbed version already
 * exists. Otherwise the object is grabbed and the cache is
 * updated.</p>
 *
 * If the underlying grabber is modified or replaced, the cache is not
 * refreshed automatically. This has to be done explicitly.</p>
 */

public class CachedGrabber implements GrabberIF, CachedIF {

  protected GrabberIF grabber;
  protected Map cache = new HashMap();
  
  public CachedGrabber(GrabberIF grabber) {
    this.grabber = grabber;
  }

  /**
   * Gets the grabber that is being cached.
   */
  public GrabberIF getGrabber() {
    return grabber;
  }
  
  /**
   * Sets the grabber that is to be cached. Note that the cache is not
   * refreshed. If the cache is to be cleared call the refresh()
   * method explicitly.
   */
  public void setGrabber(GrabberIF grabber) {
    this.grabber = grabber;
  }
  
  public Object grab(Object object) {
    if (object == null) return null;
    if (cache.containsKey(object)) return cache.get(object);
    Object grabbed = grabber.grab(object);
    cache.put(object, grabbed);
    return grabbed;
  }
  
  public void refresh() {
    cache.clear();
  }

}




