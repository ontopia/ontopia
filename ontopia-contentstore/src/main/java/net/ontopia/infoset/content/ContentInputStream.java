/*
 * #!
 * Ontopia Content Store
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

package net.ontopia.infoset.content;

import java.io.IOException;
import java.io.InputStream;

/**
 * INTERNAL: 
 */

public class ContentInputStream extends InputStream {

  protected InputStream stream;
  protected int length;
  
  public ContentInputStream(InputStream stream, int length) {
    this.stream = stream;
    this.length = length;
  }

  public int getLength() {
    return length;
  }
  
  // delegate calls to InputStream
  
  public int available() throws IOException {
    return stream.available();
  }

  public void close() throws IOException {
    stream.close();
  }
  
  public void mark(int readlimit) {
    stream.mark(readlimit);
  }

  public boolean markSupported() {
    return stream.markSupported();
  }
  
  public int read() throws IOException {
    return stream.read();
  }
  
  public int read(byte[] b) throws IOException {
    return stream.read(b);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    return stream.read(b, off, len);
  }
  
  public void reset() throws IOException {
    stream.reset();
  }
  
  public long skip(long n) throws IOException {
    return stream.skip(n);
  }
  
}
