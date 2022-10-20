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

import java.io.FilterReader;
import java.io.Reader;
import java.io.IOException;

/**
 * INTERNAL: Reader that knows its length.
 *
 * @since 4.0
 */

public class ContentReader extends FilterReader {

  protected long length;
  protected long lengthRead;
  protected boolean closed;
  
  public ContentReader(Reader reader, long length) {
    super(reader);
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
  public int read(char[] cbuf) throws IOException {
    int result = super.read(cbuf);
    if (result != -1) {
      lengthRead += result;
    }
    if (result == -1 || lengthRead >= length) {
      close();
    }
    
    return result;
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int result = super.read(cbuf, off, len);
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
		// IMPORTANT: don't call super here as this will close the
    // enclosed stream. we want the user to close it instead.
    //!super.close();
  }
  
}
