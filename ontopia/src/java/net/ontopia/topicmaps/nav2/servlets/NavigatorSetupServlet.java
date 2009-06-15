// $Id: NavigatorSetupServlet.java,v 1.3 2004/11/19 12:52:48 grove Exp $

package net.ontopia.topicmaps.nav2.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;

/**
 * INTERNAL: NavigatorSetupServlet initializes and finalizes
 * the management of the topicmaps available to the web-application.
 * </p>
 *
 * <p><i>Note:</i> This servlet will never be called anytime by
 * user request, but optionally on start-up and shut-down
 * of the web-container. If you not make use of this servlet
 * the root-level context-tag will care about setting up. 
 */
public class NavigatorSetupServlet extends HttpServlet {

  /**
   * The debugging detail level for this servlet.
   */
  private int debug = 0;

  // ---------------------------------------------------------
  // Overwrite HttpServlet Methods
  // ---------------------------------------------------------

  /**
   * Initialize this servlet, including loading the as autoload specified
   * topicmaps.  The following servlet initialization parameters
   * are processed, with default values in square brackets:
   * <ul>
   * <li><strong>debug</strong> - The debugging detail level for this
   *     servlet, which controls how much information is logged.  [0]
   * </ul>
   *
   * @exception ServletException if we cannot configure ourselves correctly
   */
  public void init() throws ServletException {
    // Process our servlet initialization parameters
    String value;
    value = getServletConfig().getInitParameter("debug");
    try {
      debug = Integer.parseInt(value);
    } catch (Throwable t) {
      debug = 0;
    }
    if (debug >= 1)
      log("Initializing navigator setup servlet.");

    // Make sure that the navigator application is initialized on startup
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(getServletContext());
  }

  /**
   * Gracefully shut down this navigator setup servlet, releasing any resources
   * that were allocated at initialization.
   */
  public void destroy() {
    if (debug >= 1)
      log("Finalizing navigator setup servlet.");

    // get navigator application
    NavigatorApplicationIF navApp =
      (NavigatorApplicationIF) getServletContext().getAttribute(NavigatorApplicationIF.NAV_APP_KEY);

    if (navApp != null) {
      // remove the registry from our application attributes
      getServletContext().removeAttribute(NavigatorApplicationIF.NAV_APP_KEY);
      
      // close navigator application
      navApp.close();
    }
  }
  
  // ---------------------------------------------------------
  // Public Methods
  // ---------------------------------------------------------

  /**
   * Return the debugging detail level for this servlet.
   */
  public int getDebug() {
    return debug;
  }

}
