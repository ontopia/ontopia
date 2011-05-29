
package net.ontopia.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * INTERNAL: Output stream for reading in a encrypted input stream (for
 * example from a file) and giving back the decrypted values.
 */
public class EncryptedOutputStream extends OutputStream {

  final static int KEY = 0xFF;
  OutputStream myOutput;
  
  public EncryptedOutputStream(OutputStream myOutputStream) {
    super();
    this.myOutput = myOutputStream;
  }

  public void write(int b) throws IOException {
    int cipher = b ^ KEY;
    myOutput.write(cipher);
  }
  
}
