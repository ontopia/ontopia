// $Id: JorgeServlet.java,v 1.5 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.generic;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

/** 
 * Servlet which implements a JSP interpreter. It generates a JSP
 * fragment and redirects to it.
 */
public class JorgeServlet extends HttpServlet {

  // Define a logging category.
  static Logger log = Logger.getLogger(JorgeServlet.class.getName());

  /**
   * Handles get requests.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response){
    processRequest(request, response);
  }
  
  /**
   * Handles post requests.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response){
    processRequest(request, response);
  }
        
  /**
   * Process Request
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
    try {
      String followingPage = (String) request.getParameter("followingPage");
      String codeFilename = (String) request.getParameter("codeFilename");
      String codeJSP = (String) request.getParameter("codeJSP");

      // full path from server root
      String applicationPath = getServletConfig().getServletContext().getRealPath("");

      if ((followingPage!=null && followingPage.length()>0)
          && (codeFilename!=null && codeFilename.length()>0)) { 

        // write out code snippet
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter( applicationPath + codeFilename )));
        writer.println( codeJSP );
        writer.close();

        // touch original JSP to force recompilation
        new File(applicationPath + followingPage).setLastModified( System.currentTimeMillis() );
        
        // send request to following page which includes JSP Code from form
        RequestDispatcher dispatcher = null;
        log.info("JorgeServlet - Dispatching Request to: " + followingPage);
        dispatcher = getServletConfig().getServletContext().getRequestDispatcher( followingPage );
        dispatcher.forward(request, response);
      }
      
    } catch (IOException e1) {
      log.warn("JorgeServlet - problems writing to file", e1);
    } catch (ServletException e2) {
      log.warn("JorgeServlet - ServletException", e2);
    }
  }


}





