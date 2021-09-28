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

package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * INTERNAL: Fake the ServletResponse, needed for execution of a servlet/JSP.
 */
public class FakeServletResponse implements HttpServletResponse {

  // FIXME: Only partially implemented.
  
  private PrintWriter printWriter;
  private int status;
  private String statusMessage;

  public FakeServletResponse() {
    this(new PrintWriter(new StringWriter()));
  }

  public FakeServletResponse(PrintWriter printWriter) {
    this.printWriter = printWriter;
    this.status = 200; // we default to OK
  }
  
  @Override
  public void flushBuffer() {
  }
  
  @Override
  public int getBufferSize() {
    return -1;
  }
  
  @Override
  public String getCharacterEncoding() {
    return null;
  }
  
  @Override
  public Locale getLocale() {
    return null;
  }
  
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }
  
  @Override
  public PrintWriter getWriter() throws IOException {
    return printWriter;
  }
  
  @Override
  public boolean isCommitted() {
    return false;
  }
  
  @Override
  public void reset() {
  }

  @Override
  public void resetBuffer() {
  }
  
  @Override
  public void setBufferSize(int size) {
  }
  
  @Override
  public void setContentLength(int len) {
  }

  @Override
  public void setContentType(java.lang.String type) {
  }

  @Override
  public void setLocale(java.util.Locale loc) {
  }

  // http methods

  @Override
  public void addCookie(Cookie cookie) {
  }

  @Override
  public void addDateHeader(String name, long date) {
  }

  @Override
  public void addHeader(String name, String value) {
  }

  @Override
  public void addIntHeader(String name, int value) {
  }
  
  @Override
  public boolean containsHeader(String name) {
    return false;
  }
  
  @Override
  public String encodeRedirectUrl(String url) {
    return url;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return url;
  }
  
  @Override
  public String encodeUrl(String url) {
    return url;
  }
  
  @Override
  public String encodeURL(String url) {
    return url;
  }
  
  @Override
  public void sendError(int sc) {
    status = sc;
  }
  
  @Override
  public void sendError(int sc, String msg) {
    status = sc;
    statusMessage = msg;
  }
  
  @Override
  public void sendRedirect(String location) {
  }
  
  @Override
  public void setDateHeader(String name, long date) {
  }
  
  @Override
  public void setHeader(String name, String value) {
  }
  
  @Override
  public void setIntHeader(String name, int value) {
  }
            
  @Override
  public void setStatus(int code) {
    this.status = code;
  }
  
  @Override
  public void setStatus(int code, String msg) {
    this.status = code;
    this.statusMessage = msg;
  }

  // --- Extra utility methods
  
  @Override
  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return statusMessage;
  }

  // servlets 2.4

  @Override
  public void setCharacterEncoding(String charset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getContentType() {
    throw new UnsupportedOperationException();
  }

}
