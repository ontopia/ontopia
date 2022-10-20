/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.servlets;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
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
  @Override
  public void init() throws ServletException {
    // Process our servlet initialization parameters
    String value;
    value = getServletConfig().getInitParameter("debug");
    try {
      debug = Integer.parseInt(value);
    } catch (Throwable t) {
      debug = 0;
    }
    if (debug >= 1) {
      log("Initializing navigator setup servlet.");
    }

    // Make sure that the navigator application is initialized on startup
    NavigatorUtils.getNavigatorApplication(getServletContext());
  }

  /**
   * Gracefully shut down this navigator setup servlet, releasing any resources
   * that were allocated at initialization.
   */
  @Override
  public void destroy() {
    if (debug >= 1) {
      log("Finalizing navigator setup servlet.");
    }

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
