
// $Id: NamedLock.java,v 1.7 2007/10/02 14:28:09 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Date;
import java.util.Collection;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * INTERNAL.
 */
public class NamedLock {
  public String name;
  public UserIF user;
  public Collection objects;
  public long timestamp;
  private long timeCreated;

  public NamedLock(String name, UserIF user, Collection objects) {
    this.name = name;
    this.user = user;
    this.objects = objects;
    this.timeCreated = System.currentTimeMillis();
  }

  public void setExpiry(HttpSession session) {
    if (!NamedLockManager.usesTimedLockExpiry(session))
      return;

    this.timestamp = System.currentTimeMillis()
        + (session.getMaxInactiveInterval() * 1000);
  }

  public boolean hasExpired() {
    return timestamp <= System.currentTimeMillis();
  }
  
  public UserIF getUser() {
    return user;
  }

  public String getTimeCreated() {
    Date locktime = new Date(timeCreated);
    SimpleDateFormat formatter =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return formatter.format(locktime);
  }
}
