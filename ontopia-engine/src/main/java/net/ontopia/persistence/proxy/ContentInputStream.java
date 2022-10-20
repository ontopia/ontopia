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

package net.ontopia.persistence.proxy;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * INTERNAL: InputStream that knows its length.
 *
 * @since 4.0
 */

public class ContentInputStream extends FilterInputStream {

  protected long length;
  protected long lengthRead;
  protected boolean closed;
  
  public ContentInputStream(InputStream stream, long length) {
    super(stream);
    this.length = length;
  }

  public long getLength() {
    return length;
  }

  @Override
  public int read() throws IOException {
    int result = super.read();
    if (result != -1) {
      lengthRead++;
    }
    if (result == -1 || lengthRead >= length) {
      close();
    }
    return result;
  }

  @Override
  public int read(byte[] bbuf) throws IOException {
    int result = super.read(bbuf);
    if (result != -1) {
      lengthRead += result;
    }
    if (result == -1 || lengthRead >= length) {
      close();
    }
    
    return result;
  }

  @Override
  public int read(byte[] bbuf, int off, int len) throws IOException {
    int result = super.read(bbuf, off, len);
    if (result != -1) {
      lengthRead += result;
    }
    if (result == -1 || lengthRead >= length) {
      close();
    }
    return result;
  }

  @Override
  public void close() throws IOException {
    if (closed) {
      return;
    }
    closed = true;
    super.close();
  }
  
}
