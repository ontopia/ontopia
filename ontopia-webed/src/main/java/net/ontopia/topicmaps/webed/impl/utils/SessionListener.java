
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

  public void sessionCreated(HttpSessionEvent event) {
  // Do nothing, we are not interested in this information at the moment
  }

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
