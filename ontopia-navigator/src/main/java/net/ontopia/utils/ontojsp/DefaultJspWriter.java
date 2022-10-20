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
    
  @Override
  public final void clear() throws IOException {
    // no-op
  }

  @Override
  public void clearBuffer() throws IOException {
    // no-op
  }

  @Override
  public void flush()  throws IOException {
    synchronized (lock) {
      out.flush();
    }
  }

  @Override
  public void close() throws IOException {
    synchronized (lock) {
      out.close();
      out = null;
    }
  }

  @Override
  public int getRemaining() {
    return -1;
  }

  @Override
  public void newLine() throws IOException {
    synchronized (lock) {
      out.println();
    }
  }

  @Override
  public void write(char[] ch, int start, int length) throws IOException {
    for (int ix = 0; ix < length; ix++) {
      out.print(ch[ix + start]);
    }
  }

  @Override
  public void print(boolean b) throws IOException {
    out.print(b);
  }

  @Override
  public void print(char c) throws IOException {
    out.print(c);
  }

  @Override
  public void print(int i) throws IOException {
    out.print(i);
  }

  @Override
  public void print(long l) throws IOException {
    out.print(l);
  }

  @Override
  public void print(float f) throws IOException {
    out.print(f);
  }

  @Override
  public void print(double d) throws IOException {
    out.print(d);
  }

  @Override
  public void print(char s[]) throws IOException {
    out.print(s);
  }

  @Override
  public void print(String s) throws IOException {
    out.print(s);
  }

  @Override
  public void print(Object obj) throws IOException {
    out.print(obj);
  }

  @Override
  public void println() throws IOException {
    newLine();
  }

  /**
   * Prints a boolean value and then terminate the line.  This method behaves
   * as though it invokes <code>{@link #print(boolean)}</code> and then
   * <code>{@link #println()}</code>.
   */
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
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
  @Override
  public void println(Object x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

}
