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

package net.ontopia.topicmaps.nav2.plugins;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * INTERNAL: Servlet that returns a PNG image.
 */
public class PNGTreeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    processRequest(request, response);
  }
  
  @Override
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





