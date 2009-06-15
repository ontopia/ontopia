
// $Id: Base64Decoder.java,v 1.10 2008/03/25 12:16:30 geir.gronmo Exp $

package net.ontopia.net;

import java.io.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: This class contains methods for encoding base64
 * streams. Base64 encoding is described in section 6.8 of RFC 2045.
 */
public class Base64Decoder {
  private static char[] map =
  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
   'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
   'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
   'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
   's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
   '3', '4', '5', '6', '7', '8', '9', '+', '/'};

  private static int[] invmap;

  private static final int BUFFER_SIZE = 1024 ;
  
  /**
   * INTERNAL: Decode string and return result as a string.
   */
  public static String decode(String string) throws IOException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    decode(string, ostream);
    return ostream.toString("ISO-8859-1");
  }
  
  /**
   * INTERNAL: Decode string and write result to output stream.
   */
  public static void decode(String string, OutputStream ostream) throws IOException {
    decode(new ByteArrayInputStream(string.getBytes("ISO-8859-1")), ostream);
  }
 
  /**
   * INTERNAL: Decodes the characters in input to the output byte
   * array, returning the number of bytes written. If the output array
   * is too short, that's just too bad.     
   */
  public static int decode(char[] input, int offset, int length,
                           byte[] output) {
    if (invmap == null) {			   
      invmap = new int[256];
      for (int ix = 0; ix < invmap.length; ix++)
        invmap[ix] = -1;
      for (int ix = 0; ix < map.length; ix++)
        invmap[(byte) map[ix]] = ix;
    }

    byte[] octets = new byte[4];
    int octetCount = 0;
    int outpos = 0;
    
    for (int ix = 0; ix < length; ix++)
      if (input[ix] == '=')
        break;
      else if (invmap[input[ix]] != -1) {
        octets[octetCount++] = (byte) invmap[input[ix]];
        if (octetCount == 4) {
          output[outpos++] = (byte) ((octets[0] << 2) |
                                     ((octets[1] & 0x30) >> 4));
          output[outpos++] = (byte) (((octets[1] & 0x0F) << 4) |
                                     ((octets[2] & 0x3C) >> 2));
          output[outpos++] = (byte) (((octets[2] & 0x03) << 6) |
                                     (octets[3] & 0x3F));
          octetCount = 0;
        }
      }

    if (octetCount > 1) 
      output[outpos++] = (byte) ((octets[0] << 2) |
                                 ((octets[1] & 0x30) >> 4));
    if (octetCount > 2) 
      output[outpos++] = (byte) (((octets[1] & 0x0F) << 4) |
                                 ((octets[2] & 0x3C) >> 2));
    
    return outpos;   
  }
 
  /**
   * INTERNAL: Decode input stream and write result to output stream.
   */
  public static void decode(InputStream istream, OutputStream ostream) throws IOException {
      
    byte buffer[] = new byte[BUFFER_SIZE] ;
    byte chunk[]  = new byte[4] ;
    int  got      = -1 ;
    int  ready    = 0 ;
	
    fill:
    while ((got = istream.read(buffer)) > 0) {
	    int skiped = 0 ;
	    while ( skiped < got ) {
        // Check for un-understood characters:
        while ( ready < 4 ) {
          if ( skiped >= got )
            continue fill ;
          int ch = check (buffer[skiped++]) ;
          if ( ch >= 0 )
            chunk[ready++] = (byte) ch ;
        }
        if ( chunk[2] == 65 ) {
          ostream.write(get1(chunk, 0));
          return ;
        } else if ( chunk[3] == 65 ) {
          ostream.write(get1(chunk, 0)) ;
          ostream.write(get2(chunk, 0)) ;
          return ;
        } else {
          ostream.write(get1(chunk, 0)) ;
          ostream.write(get2(chunk, 0)) ;
          ostream.write(get3(chunk, 0)) ;
        }
        ready = 0 ;
	    } 
    }
    if (ready != 0)
	    throw new OntopiaRuntimeException ("Invalid length.") ;
    ostream.flush() ;
  }

  private static final int get1 (byte buf[], int off) {
    return ((buf[off] & 0x3f) << 2) | ((buf[off+1] & 0x30) >>> 4) ;
  }

  private static final int get2 (byte buf[], int off) {
    return ((buf[off+1] & 0x0f) << 4) | ((buf[off+2] &0x3c) >>> 2) ;
  }

  private static final int get3 (byte buf[], int off) {
    return ((buf[off+2] & 0x03) << 6) | (buf[off+3] & 0x3f) ;
  }

  private static final int check (int ch) {
    if ((ch >= 'A') && (ch <= 'Z')) {
	    return ch - 'A' ;
    } else if ((ch >= 'a') && (ch <= 'z')) {
	    return ch - 'a' + 26 ;
    } else if ((ch >= '0') && (ch <= '9')) {
	    return ch - '0' + 52 ;
    } else {
	    switch (ch) {
      case '=':
        return 65 ;
      case '+':
        return 62 ;
      case '/':
        return 63 ;
      default:
        return -1 ;
	    }
    }
  }
  
}
