
// $Id: StreamUtilsTest.java,v 1.1 2003/10/20 13:12:57 larsga Exp $

package net.ontopia.utils.test;

import java.io.*;
import net.ontopia.test.*;
import net.ontopia.utils.*;

public class StreamUtilsTest extends AbstractOntopiaTestCase {
  
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
