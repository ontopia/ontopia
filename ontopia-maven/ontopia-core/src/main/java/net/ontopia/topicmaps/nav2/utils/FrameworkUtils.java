
// $Id: FrameworkUtils.java,v 1.18 2005/06/21 08:53:22 ian Exp $

package net.ontopia.topicmaps.nav2.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.User;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related utility class providing some helper
 * methods needed to easier for examples access user information.
 */
public final class FrameworkUtils {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(FrameworkUtils.class.getName());

  /**
   * INTERNAL: Gets the context tag from the request scope.
   */
  public static ContextTag getContextTag(PageContext pageContext) {
    return getContextTag(pageContext.getRequest());
  }
  
  /**
   * INTERNAL: Gets the context tag from the request.
   */
  public static ContextTag getContextTag(ServletRequest request) {
    return (ContextTag)
      request.getAttribute(NavigatorApplicationIF.CONTEXT_KEY);
  }

  /**
   * INTERNAL: Gets user object out of session scope.
   */
  public static UserIF getUser(PageContext pageContext) {
    return getUser(pageContext, true);
  }

  /**
   * INTERNAL: Gets user object out of session scope.
   */
  public static UserIF getUser(PageContext pageContext, boolean create) {
    try {
      Object obj = pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
					    PageContext.SESSION_SCOPE);
      if (obj != null && obj instanceof UserIF)
	return (UserIF) obj;
      else
	// if no user object exists just create a new one
	return (create ? createUserSession(pageContext) : null);

    } catch (java.lang.IllegalStateException e) {

      // sessions not allowed in page, so get the user from the request scope instead
      Object obj = pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
					    PageContext.REQUEST_SCOPE);
      if (obj != null && obj instanceof UserIF)
	return (UserIF) obj;
      else
	// if no user object exists just create a new one
	return (create ? createUserSession(pageContext, PageContext.REQUEST_SCOPE) : null);

    }
  }

  /**
   * INTERNAL: Create new user object in session scope.
   */
  public static UserIF createUserSession(PageContext pageContext) {
    return createUserSession(pageContext, PageContext.SESSION_SCOPE);
  }

  /**
   * INTERNAL: Create new user object in given scope.
   */
  public static UserIF createUserSession(PageContext pageContext, int scope) {

    NavigatorConfigurationIF navConf = NavigatorUtils.getNavigatorApplication(pageContext).getConfiguration();
    // try to retrieve the user name from the request, otherwise null
    String username = null;
    if (pageContext.getRequest() instanceof HttpServletRequest)
      username = ((HttpServletRequest) pageContext.getRequest()).getRemoteUser();
    // create new user object
    UserIF user = new User(username, navConf);
    // set MVS settings
    user = setDefaultMVS(navConf, user);
    // set user object to session scope
    pageContext.setAttribute(NavigatorApplicationIF.USER_KEY, user, scope);
    log.info("New user object ('" + user.getId() + "') created and bound it in scope ( " + scope + ").");
    return user;
  }

  /**
   * INTERNAL: Reset MVS settings in user object in session scope.
   */
  public static void resetMVSsettingsInUserSession(PageContext pageContext) {
    NavigatorConfigurationIF navConf = NavigatorUtils.getNavigatorApplication(pageContext).getConfiguration();

    //! UserIF user = (UserIF) pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
    //!                                                 PageContext.SESSION_SCOPE);
    UserIF user = getUser(pageContext, false);
    if (user == null) return; // ignore if no user

    // reset MVS settings
    user = setDefaultMVS(navConf, user);
    // set user object to session scope (TODO: support other contexts?)
    pageContext.setAttribute(NavigatorApplicationIF.USER_KEY, user, PageContext.SESSION_SCOPE);
    log.info("MVS settings in user session has been reset.");
  }

  
  // ------------------------------------------------------------
  // internal helper methods
  // ------------------------------------------------------------

  /**
   * INTERNAL: Resets MVS settings for given user object.
   */
  private static UserIF setDefaultMVS(NavigatorConfigurationIF navConf,
                                      UserIF user) {
    // get defaults from config and set them
    if (navConf.getDefaultModel() != null
        && !navConf.getDefaultModel().equals(""))
      user.setModel( navConf.getDefaultModel() );
    if (navConf.getDefaultView() != null
        && !navConf.getDefaultView().equals(""))
      user.setView( navConf.getDefaultView()  );
    if (navConf.getDefaultSkin() != null
        && !navConf.getDefaultSkin().equals(""))
      user.setSkin( navConf.getDefaultSkin() );
    
    return user;
  }
  
}
