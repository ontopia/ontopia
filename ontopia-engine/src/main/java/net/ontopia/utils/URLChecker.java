/*
 * #!
 * Ontopia Engine
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

package net.ontopia.utils;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * INTERNAL: Tries to establish a connection to URL and check whether
 * it could be retrieved or is not reachable.
 */
public class URLChecker {

  private static URL url;
  private static URLConnection urlConn;
  private static InputStream urlInputStream;
  private static String serverResponse;

  /**
   * returns false if no connection could be established
   */
  public static synchronized boolean isAlive(String strURL)
    throws MalformedURLException, IOException  {

    url = new URL(strURL);

    if (!url.getProtocol().equalsIgnoreCase("HTTP") ) {
      throw new IllegalArgumentException("Only support HTTP Protocol");
    }

    // try to open the connection and retrieve only header
    urlConn = url.openConnection();
    urlConn.setUseCaches(false);
    urlInputStream = urlConn.getInputStream();
    serverResponse = urlConn.getHeaderField(0);
    // for example "HTTP/1.0 404 Not Found"
    if (serverResponse == null) {
      return false;
    }
    if (serverResponse.indexOf("404") >= 0) {
      return false;
    }
    
    return true;
  }

  
  /**
   * display some information about the retrieved Header of the URL
   */
  private static void displayHeader() {
    System.out.println("URL:              " + url.toExternalForm() );
    System.out.println("Server response:  " + serverResponse );
    System.out.println("Server:           " + urlConn.getHeaderField("Server") );
    System.out.println("Content-type:     " + urlConn.getHeaderField("Content-type") );
    System.out.println("Content-encoding: " + urlConn.getHeaderField("Content-encoding") );
    System.out.println("Accept-Ranges:    " + urlConn.getHeaderField("Accept-Ranges") );
    System.out.println("Last modified:    " + new java.util.Date(urlConn.getLastModified()) );
    System.out.println("Content-length:   " + urlConn.getContentLength() );
  }

  /**
   * only for test purposes
   */
  public static void main(String args[]) {
    try {
      System.out.println( "isAlive? " + URLChecker.isAlive( args[0] ));
      displayHeader();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  
}




