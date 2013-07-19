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
import junit.framework.TestCase;

public class StreamUtilsTest extends TestCase {
  
  public StreamUtilsTest(String name) {
    super(name);
  }

  // --- byte[] read test cases

  public void testSmallRead() throws IOException {
    byte[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    byte[] read = StreamUtils.read(new ByteArrayInputStream(data), data.length);

    assertTrue("Retrieved byte array of different length",
               data.length == read.length);

    for (int ix = 0; ix < data.length; ix++)
      assertTrue("Retrieved byte array has '" + read[ix] + "', while data has '" +
                 data[ix] + "' in position " + ix, read[ix] == data[ix]);
  }

  public void testEmptyRead() throws IOException {
    byte[] data = {};
    byte[] read = StreamUtils.read(new ByteArrayInputStream(data), data.length);

    assertTrue("Retrieved byte array of different length",
               data.length == read.length);
  }

  public void testBigRead() throws IOException {
    int BUF_SIZE = 16384;
    
    byte[] data = new byte[BUF_SIZE + 17];
    for (int ix = 0; ix < data.length; ix++)
      data[ix] = (byte) ix;
    
    byte[] read = StreamUtils.read(new ByteArrayInputStream(data), data.length);

    assertTrue("Retrieved byte array of different length",
               data.length == read.length);
    
    for (int ix = 0; ix < data.length; ix++)
      assertTrue("Retrieved byte array has '" + read[ix] + "', while data has '" +
                 data[ix] + "' in position " + ix, read[ix] == data[ix]);
  }
  
}
