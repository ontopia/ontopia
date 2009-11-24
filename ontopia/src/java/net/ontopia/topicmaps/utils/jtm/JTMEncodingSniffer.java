package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.PushbackInputStream;
import net.ontopia.topicmaps.impl.utils.EncodingSnifferIF;

/**
 * INTERNAL: An encoding sniffer for JTM.
 */
public class JTMEncodingSniffer implements EncodingSnifferIF {
  
  public String guessEncoding(PushbackInputStream stream) throws IOException {
    String encoding;

    // Look to see if there's a UTF-8 BOM (Byte Order Mark) at the
    // start of the stream.
    byte[] bomBuffer = new byte[3];
    boolean foundBom = false;
    int bytesread = stream.read(bomBuffer, 0, 3);    
    if (bytesread == 3) {
      // Check if bomBuffer contains the UTF-8 BOM. Casts necessary to deal
      // with signedness issues. (Java needs unsigned byte!)
      foundBom = (bomBuffer[0] == (byte) 0xEF &&
                  bomBuffer[1] == (byte) 0xBB &&
                  bomBuffer[2] == (byte) 0xBF);
      
      if (!foundBom)
        stream.unread(bomBuffer, 0, 3);
    } 
    if (foundBom) 
      encoding = "utf-8";
    else
      encoding = "iso-8859-1";
    
    return encoding;
  }  
}
