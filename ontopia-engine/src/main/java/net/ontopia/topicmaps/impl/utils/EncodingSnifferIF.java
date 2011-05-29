
package net.ontopia.topicmaps.impl.utils;

import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * INTERNAL: An object which can guess the encoding of an input stream
 * by peeking into its contents. Sniffers are specific to a syntax.
 */
public interface EncodingSnifferIF {

  /**
   * INTERNAL: By examining the contents of the stream, guess the
   * encoding used.
   * @return the name of the encoding
   */
  public String guessEncoding(PushbackInputStream stream) throws IOException;
  
}
