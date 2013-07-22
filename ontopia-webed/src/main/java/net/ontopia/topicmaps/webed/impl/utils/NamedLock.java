/*
 * #!
 * Ontopia Webed
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
