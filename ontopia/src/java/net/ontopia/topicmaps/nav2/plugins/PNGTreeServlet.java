// $Id: PNGTreeServlet.java,v 1.10 2004/11/12 11:24:52 grove Exp $

package net.ontopia.topicmaps.nav2.plugins;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * INTERNAL: Servlet that returns a PNG image.
 */
public class PNGTreeServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    processRequest(request, response);
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    processRequest(request, response);
  }
        
  protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
    try {
      response.setContentType("image/gif");

      byte[] image = (byte[]) request.getSession().getAttribute("image");
      OutputStream out = response.getOutputStream();
      for (int pos = 0; pos < image.length; pos += 16384) {
        int len = Math.min(16384, image.length - pos);
        out.write(image, pos, len);
      }
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}





