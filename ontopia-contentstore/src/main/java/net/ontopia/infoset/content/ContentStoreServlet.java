/*
 * #!
 * Ontopia Content Store
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

package net.ontopia.infoset.content;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: A servlet implementation that returns content stored in a
 * content store. It supports the following interface:</p>
 *
 * <pre>
 * Parameter   Explanation                                       Required
 * ---------   ------------------------------------------------  --------
 * uri         The URI of the content object, which must be of   Yes
 *             the form "x-ontopia:cms:XXX".
 * 
 * tmid        The identifier of the topic map from which the    Yes
 *             CO has been referenced.
 * 
 * ctype       The content-type to be given for the CO. Default  No
 *             is "application/octet-stream".
 * 
 * view        Whether to tell browser to open in browser        No
 *             window or to download into file. Values: 
 *             true/false. Default is to download to file 
 *             (false). 
 * 
 * filename    The filename to suggest. Default is to suggest    No
 *             none.
 * </pre>
 */
public class ContentStoreServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {
    // default values
    String ctype = "application/octet-stream";
    boolean view = false;

    // get values from request
    String uri = request.getParameter("uri");
    if (uri == null) {
      throw new ServletException("Request parameter 'uri' required");
    }
    
    String tmid = request.getParameter("tmid");
    if (tmid == null) {
      throw new ServletException("Request parameter 'tmid' required");
    }
    
    String filename = request.getParameter("filename");

    if (request.getParameter("ctype") != null) {
      ctype = request.getParameter("ctype");
    }

    if (request.getParameter("view") != null) {
      String value = request.getParameter("ctype").trim().toLowerCase();
      if ("true".equals(value)) {
        view = true;
      } else if (!"false".equals(value)) {
        throw new ServletException("Request parameter 'view' must hold 'true' or 'false'");
      }
    }
    
    // find the key
    if (!uri.startsWith("x-ontopia:cms:")) {
      throw new ServletException("URI must begin with 'x-ontopia:cms:'");
    }
    int key;
    try {
      key = Integer.parseInt(uri.substring(14));
    } catch (NumberFormatException e) {
      throw new ServletException("Content object ID was not an integer: '" +
                                 uri.substring(14) + "'");
    }

    // set response headers (content-disposition defined in RFC 2183)
    response.setContentType(ctype);
    String disposition = "attachment";
    if (view) {
      disposition = "inline";
    }
    if (filename != null) {
      disposition = disposition + "; filename=" + filename;
    }
    response.setHeader("Content-disposition", disposition);

    // get the content store
    NavigatorApplicationIF navApp = null;
    TopicMapIF tm = null;
    try {
      navApp = NavigatorUtils.getNavigatorApplication(getServletContext());
      tm = navApp.getTopicMapById(tmid);
      ContentStoreIF cs = getContentStore(tm, getServletContext());
    
      // get the content stream
      ContentInputStream istream;
      try {
        istream = cs.get(key);
      } catch (ContentStoreException e) {
        throw new ServletException(e);
      }
    
      // set the content length
      response.setContentLength(istream.getLength()); 
    
      // write content to output stream
      try {
        IOUtils.copy(istream, response.getOutputStream());
        istream.close();
      } catch (IOException e) {
        throw new ServletException(e);
      }
    } catch (NavigatorRuntimeException e) {
      throw new ServletException(e);
    } catch (ContentStoreException e) {
      throw new ServletException(e);
    } finally {
      if (navApp != null && tm != null) {
        navApp.returnTopicMap(tm);
      }
    }
  }

  // --- To be overridden, if necessary

  protected ContentStoreIF getContentStore(TopicMapIF tm, ServletContext ctxt)
    throws ContentStoreException {
    return ContentStoreUtils.getContentStore(tm, null);
  }
  
}
