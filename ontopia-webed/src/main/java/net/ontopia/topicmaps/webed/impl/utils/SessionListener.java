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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.UserIF;

/**
 * INTERNAL: Class to listen when sessions are created and destroyed and keep
 * the associated lock manager up to date.
 */

public class SessionListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent event) {
  // Do nothing, we are not interested in this information at the moment
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    HttpSession session = event.getSession();
    UserIF user = (UserIF)session
        .getAttribute(NavigatorApplicationIF.USER_KEY);
    if (user != null) {
      NamedLockManager lockManager = TagUtils.getNamedLockManager(session
          .getServletContext());
      lockManager.releaseLocksFor(user);
    }
  }
}
