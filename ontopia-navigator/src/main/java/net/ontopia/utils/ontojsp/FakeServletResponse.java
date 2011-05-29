
package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
  
  public void flushBuffer() {
  }
  
  public int getBufferSize() {
    return -1;
  }
  
  public String getCharacterEncoding() {
    return null;
  }
  
  public Locale getLocale() {
    return null;
  }
  
  public ServletOutputStream getOutputStream() throws IOException {
    return null;
  }
  
  public PrintWriter getWriter() throws IOException {
    return printWriter;
  }
  
  public boolean isCommitted() {
    return false;
  }
  
  public void reset() {
  }

  public void resetBuffer() {
  }
  
  public void setBufferSize(int size) {
  }
  
  public void setContentLength(int len) {
  }

  public void setContentType(java.lang.String type) {
  }

  public void setLocale(java.util.Locale loc) {
  }

  // http methods

  public void addCookie(Cookie cookie) {
  }

  public void addDateHeader(String name, long date) {
  }

  public void addHeader(String name, String value) {
  }

  public void addIntHeader(String name, int value) {
  }
  
  public boolean containsHeader(String name) {
    return false;
  }
  
  public String encodeRedirectUrl(String url) {
    return url;
  }

  public String encodeRedirectURL(String url) {
    return url;
  }
  
  public String encodeUrl(String url) {
    return url;
  }
  
  public String encodeURL(String url) {
    return url;
  }
  
  public void sendError(int sc) {
    status = sc;
  }
  
  public void sendError(int sc, String msg) {
    status = sc;
    statusMessage = msg;
  }
  
  public void sendRedirect(String location) {
  }
  
  public void setDateHeader(String name, long date) {
  }
  
  public void setHeader(String name, String value) {
  }
  
  public void setIntHeader(String name, int value) {
  }
            
  public void setStatus(int code) {
    this.status = code;
  }
  
  public void setStatus(int code, String msg) {
    this.status = code;
    this.statusMessage = msg;
  }

  // --- Extra utility methods
  
  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return statusMessage;
  }

  // servlets 2.4

  public void setCharacterEncoding(String charset) {
    throw new UnsupportedOperationException();
  }

  public String getContentType() {
    throw new UnsupportedOperationException();
  }

}
