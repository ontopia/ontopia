
// $Id: ContentStoreServlet.java,v 1.6 2004/11/19 12:52:46 grove Exp $

package net.ontopia.infoset.content;

import java.io.*;
import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.persistence.proxy.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

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
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {
    // default values
    String ctype = "application/octet-stream";
    boolean view = false;

    // get values from request
    String uri = request.getParameter("uri");
    if (uri == null)
      throw new ServletException("Request parameter 'uri' required");
    
    String tmid = request.getParameter("tmid");
    if (tmid == null)
      throw new ServletException("Request parameter 'tmid' required");
    
    String filename = request.getParameter("filename");

    if (request.getParameter("ctype") != null)
      ctype = request.getParameter("ctype");

    if (request.getParameter("view") != null) {
      String value = request.getParameter("ctype").trim().toLowerCase();
      if (value.equals("true"))
        view = true;
      else if (!value.equals("false"))
        throw new ServletException("Request parameter 'view' must hold 'true' or 'false'");
    }
    
    // find the key
    if (!uri.startsWith("x-ontopia:cms:"))
      throw new ServletException("URI must begin with 'x-ontopia:cms:'");
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
    if (view)
      disposition = "inline";
    if (filename != null)
      disposition = disposition + "; filename=" + filename;
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
        OutputStream ostream = response.getOutputStream();
        StreamUtils.transfer(istream, ostream);
        istream.close();
      } catch (IOException e) {
        throw new ServletException(e);
      }
    } catch (NavigatorRuntimeException e) {
      throw new ServletException(e);
    } catch (ContentStoreException e) {
      throw new ServletException(e);
    } finally {
      if (navApp != null && tm != null)
	navApp.returnTopicMap(tm);
    }
  }

  // --- To be overridden, if necessary

  protected ContentStoreIF getContentStore(TopicMapIF tm, ServletContext ctxt)
    throws ContentStoreException {
    return ContentStoreUtils.getContentStore(tm, null);
  }
  
}
