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

package net.ontopia.topicmaps.webed.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.Parameters;
import net.ontopia.topicmaps.webed.impl.utils.ReqParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Servlet for unregistering form action data.
 */
public final class FormUnregisterServlet extends HttpServlet {

  // --- initialize logging facility.
  static Logger logger = LoggerFactory.getLogger(FormUnregisterServlet.class.getName());

  /**
   * INTERNAL: Handles an HTTP GET request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }
  
  /**
   * INTERNAL: Handles an HTTP POST request.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }
    
  /**
   * INTERNAL: Internal method handling all of the incoming HTTP requests. 
   * The following request parameters have to be available:
   * <ul>
   *   <li>Constants.RP_REQUEST_ID: holds the ID of the request being
   *   unregistered</li> 
   * </ul>
   * </p>
   * <p>The parameters will be
   */
  protected void processRequest(HttpServletRequest request, 
                                HttpServletResponse response)
    throws ServletException, IOException {

    // Get the navigator application
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(getServletContext());
    if (navApp == null) {
      logger.warn("NavigationApplication object is NOT available.");
      throw new ServletException("NavigationApplication object is NOT available.");
    }

    // ensure that request character encoding decoded correctly (bug #622)
    String charenc = navApp.getConfiguration().getProperty("defaultCharacterEncoding");
    if (charenc != null && charenc.trim().equals(""))
      charenc = null;

    // decode request into Parameters object
    Parameters params = ReqParamUtils.decodeParameters(request, charenc);

    // logging
    logrequest(params);
    
    // Get the request id.
    String requestId = params.get(Constants.RP_REQUEST_ID);
    if (requestId == null)
      throw new ServletException("The request parameter '"
          + Constants.RP_REQUEST_ID + "' must " 
          + "contain the request id of a form.");
    
    // retrieve user object from session
    UserIF user = (UserIF)
      request.getSession().getAttribute(NavigatorApplicationIF.USER_KEY);
    if (user != null)
      // Remove the request data from the cache
      user.removeWorkingBundle(requestId);
    // If the user is null then the request is already gone, so do nothing

    // At this point we have succeeded; forward to confirmation page
    String forward = params.get("forward");
    if (forward != null)
      response.sendRedirect(forward);
  }

  /**
   * Log the request parameters.
   * @param params Request parameters.
   */
  private void logrequest(Parameters params) {
    Iterator it = params.getNames().iterator();
    while (it.hasNext()) {
      String paramname = (String) it.next();
      logger.debug("Param '" + paramname + "': '" +
                   Arrays.toString(params.getValues(paramname)) + "'");
    }
  }
}
