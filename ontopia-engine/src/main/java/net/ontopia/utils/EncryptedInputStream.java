
package net.ontopia.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * INTERNAL: Input stream for reading in a encrypted input stream (for
 * example from a file) and giving back the decrypted values.
 */
public class EncryptedInputStream extends InputStream {

  final static int KEY = 0xFF;
  InputStream myInput;
  
  public EncryptedInputStream(InputStream myInputStream) {
    super();
    this.myInput = myInputStream;
  }

  public int read() throws IOException {
    int b = myInput.read();
    int plain;
    // only if not end of file is reached
    if (b != -1)
      plain = b ^ KEY;
    else
      plain = -1;
    return plain;
  }
  
}
