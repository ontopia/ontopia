
// $Id: DefaultJspWriter.java,v 1.8 2005/09/08 10:00:53 ian Exp $

package net.ontopia.utils.ontojsp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

/**
 * Fake the JspWriter, needed for execution of a JSP.
 */
public class DefaultJspWriter extends JspWriter {

  private PrintWriter out;

  // DO NOT MAKE ANY CONSTRUCTOR WITH OUTPUTSTREAM AND NO SPECIFIED
  // ENCODING. WE NEED CONTROL OVER THE ENCODING!

  public DefaultJspWriter(Writer out) {
    super(1024, false);
    this.out = new PrintWriter(out);
  }
    
  public final void clear() throws IOException {
  }

  public void clearBuffer() throws IOException {
  }

  public void flush()  throws IOException {
    synchronized (lock) {
      out.flush();
    }
  }

  public void close() throws IOException {
    synchronized (lock) {
      out.close();
      out = null;
    }
  }

  public int getRemaining() {
    return -1;
  }

  public void newLine() throws IOException {
    synchronized (lock) {
      out.println();
    }
  }

  public void write(char[] ch, int start, int length) throws IOException {
    for (int ix = 0; ix < length; ix++)
      out.print(ch[ix + start]);
  }

  public void print(boolean b) throws IOException {
    out.print(b);
  }

  public void print(char c) throws IOException {
    out.print(c);
  }

  public void print(int i) throws IOException {
    out.print(i);
  }

  public void print(long l) throws IOException {
    out.print(l);
  }

  public void print(float f) throws IOException {
    out.print(f);
  }

  public void print(double d) throws IOException {
    out.print(d);
  }

  public void print(char s[]) throws IOException {
    out.print(s);
  }

  public void print(String s) throws IOException {
    out.print(s);
  }

  public void print(Object obj) throws IOException {
    out.print(obj);
  }

  public void println() throws IOException {
    newLine();
  }

  /**
   * Prints a boolean value and then terminate the line.  This method behaves
   * as though it invokes <code>{@link #print(boolean)}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(boolean x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints a character and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
   * #println()}</code>.
   */
  public void println(char x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints an integer and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
   * #println()}</code>.
   */
  public void println(int x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints a long integer and then terminate the line.  This method behaves
   * as though it invokes <code>{@link #print(long)}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(long x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints a floating-point number and then terminate the line.  This method
   * behaves as though it invokes <code>{@link #print(float)}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(float x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints a double-precision floating-point number and then terminate the
   * line. This method behaves as though it invokes <code>{@link
   * #print(double)}</code> and then <code>{@link #println()}</code>.
   */
  public void println(double x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints an array of characters and then terminate the line.  This method
   * behaves as though it invokes <code>{@link #print(char[])}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(char x[]) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints a String and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(String)}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(String x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  /**
   * Prints an Object and then terminate the line.  This method behaves as
   * though it invokes <code>{@link #print(Object)}</code> and then
   * <code>{@link #println()}</code>.
   */
  public void println(Object x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

}
