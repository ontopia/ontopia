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

import java.io.IOException;
import java.io.InputStream;

/**
 * INTERNAL: Input stream for reading in a encrypted input stream (for
 * example from a file) and giving back the decrypted values.
 */
public class EncryptedInputStream extends InputStream {

  private final static int KEY = 0xFF;
  private InputStream myInput;
  
  public EncryptedInputStream(InputStream myInputStream) {
    super();
    this.myInput = myInputStream;
  }

  @Override
  public int read() throws IOException {
    int b = myInput.read();
    int plain;
    // only if not end of file is reached
    if (b != -1) {
      plain = b ^ KEY;
    } else {
      plain = -1;
    }
    return plain;
  }
  
}
