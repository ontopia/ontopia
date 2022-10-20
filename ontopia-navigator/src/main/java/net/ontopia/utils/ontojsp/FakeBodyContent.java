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

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;

/**
 * INTERNAL: Fake body content for use with the ontopia fake jsp
 * environment.
 */
public class FakeBodyContent extends BodyContent {

  private char[] cb;
  private int nextChar;
  private static String lineSeparator = System.getProperty("line.separator");

  public FakeBodyContent(JspWriter writer) {
    super(writer);
    bufferSize = 8*1024;
    cb = new char[bufferSize];
    nextChar = 0;
  }

  @Override
  public void write(int c) throws IOException {
    synchronized (lock) {
      if (nextChar >= bufferSize) {
        reAllocBuff (0);
      }
      cb[nextChar++] = (char) c;
    }
  }

  private void reAllocBuff (int len) {
    char[] tmp = null;
    if (len <= bufferSize){
      bufferSize *= 2;
    } else {
      bufferSize += len;
    }
    tmp = new char[bufferSize];
    System.arraycopy(cb, 0, tmp, 0, cb.length);
    cb = tmp;
  }

  @Override
  public void write(char cbuf[], int off, int len) throws IOException {
    synchronized (lock) {
      if ((off < 0) || (off > cbuf.length) || (len < 0) ||
          ((off + len) > cbuf.length) || ((off + len) < 0)) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return;
      } 
      if (len >= bufferSize - nextChar) {
        reAllocBuff (len);
      }
      System.arraycopy(cbuf, off, cb, nextChar, len);
      nextChar+=len;
    }
  }
  
  @Override
  public void write(char buf[]) throws IOException {
    write(buf, 0, buf.length);
  }

  @Override
  public void write(String s, int off, int len) throws IOException {
    synchronized (lock) {
      if (len >= bufferSize - nextChar) {
        reAllocBuff(len);
      }    
      s.getChars(off, off + len, cb, nextChar);
      nextChar += len;
    }
  }

  @Override
  public void newLine() throws IOException {
    synchronized (lock) {
      write(lineSeparator);
    }
  }

  @Override
  public void print(boolean b) throws IOException {
    write(b ? "true" : "false");
  }

  @Override
  public void print(char c) throws IOException {
    write(String.valueOf(c));
  }

  @Override
  public void print(int i) throws IOException {
    write(String.valueOf(i));
  }
 
  @Override
  public void print(long l) throws IOException {
    write(String.valueOf(l));
  }

  @Override
  public void print(float f) throws IOException {
    write(String.valueOf(f));
  }

  @Override
  public void print(double d) throws IOException {
    write(String.valueOf(d));
  }

  @Override
  public void print(char s[]) throws IOException {
    write(s);
  }

  @Override
  public void print(String s) throws IOException {
    if (s == null) {
      s = "null";
    }
    write(s);
  }

  @Override
  public void print(Object obj) throws IOException {
    write(String.valueOf(obj));
  }

  @Override
  public void println() throws IOException {
    newLine();
  }

  @Override
  public void println(boolean x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void println(char x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void println(int x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void println(long x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void println(float x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }
  
  @Override
  public void println(double x) throws IOException{
    synchronized (lock) {
      print(x);
      println();
    }
  }
  
  @Override
  public void println(char x[]) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }
  
  @Override
  public void println(String x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void println(Object x) throws IOException {
    synchronized (lock) {
      print(x);
      println();
    }
  }

  @Override
  public void clear() throws IOException {
    synchronized (lock) {
      nextChar = 0;
    }
  }

  @Override
  public void clearBuffer() throws IOException {
    this.clear();
  }

  @Override
  public void close() throws IOException {
    synchronized (lock) {
      cb = null;        
    }
  }

  @Override
  public int getRemaining() {
    return bufferSize - nextChar;
  }

  @Override
  public Reader getReader() {
    return new CharArrayReader (cb, 0, nextChar);
  }

  @Override
  public String getString() {
    return new String(cb, 0, nextChar);
  }

  @Override
  public void writeOut(Writer out) throws IOException {
    out.write(cb, 0, nextChar);
  }

}
