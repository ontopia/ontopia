
// $Id: Base64Encoder.java,v 1.7 2003/07/28 10:06:18 larsga Exp $

package net.ontopia.net;

import java.io.*;

/**
 * INTERNAL: This class contains methods for decoding base64
 * streams. Base64 decoding is described in section 6.8 of RFC 2045.
 */

public class Base64Encoder {  

  private static final int BUFFER_SIZE = 1024;
  
  private static byte[] map =
  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
   'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
   'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
   'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
   's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
   '3', '4', '5', '6', '7', '8', '9', '+', '/'};

  /**
   * INTERNAL: Encode string and return result as a string.
   */
  public static String encode(String string) throws IOException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();    
    encode(string, ostream);
    return ostream.toString("ISO-8859-1");
  }
  
  /**
   * INTERNAL: Encode string and write result to output stream.
   */
  public static void encode(String string, OutputStream ostream) throws IOException {
    encode(new ByteArrayInputStream(string.getBytes("ISO-8859-1")), ostream);
  }
  
  /**
   * INTERNAL: Encode input stream and write result to output stream.
   */
  public static void encode(InputStream istream, OutputStream ostream) throws IOException {    
    byte buffer[] = new byte[BUFFER_SIZE] ;
    int  got      = -1 ;
    int  off      = 0 ;
    int  count    = 0 ;
    while ((got = istream.read(buffer, off, BUFFER_SIZE-off)) > 0) {
	    if ( got >= 3 ) {
        got += off;
        off  = 0;
        while (off + 3 <= got) {
          int c1 = get1(buffer,off) ;
          int c2 = get2(buffer,off) ;
          int c3 = get3(buffer,off) ;
          int c4 = get4(buffer,off) ;
          switch (count) {
		      case 73:
            ostream.write(map[c1]);
            ostream.write(map[c2]);
            ostream.write(map[c3]);
            ostream.write ('\n') ;
            ostream.write(map[c4]) ;
            count = 1 ;
            break ;
		      case 74:
            ostream.write(map[c1]);
            ostream.write(map[c2]);
            ostream.write ('\n') ;
            ostream.write(map[c3]);
            ostream.write(map[c4]) ;
            count = 2 ;
            break ;
		      case 75:
            ostream.write(map[c1]);
            ostream.write ('\n') ;
            ostream.write(map[c2]);
            ostream.write(map[c3]);
            ostream.write(map[c4]) ;
            count = 3 ;
            break ;
		      case 76:
            ostream.write('\n') ;
            ostream.write(map[c1]);
            ostream.write(map[c2]);
            ostream.write(map[c3]);
            ostream.write(map[c4]) ;
            count = 4 ;
            break ;
		      default:
            ostream.write(map[c1]);
            ostream.write(map[c2]);
            ostream.write(map[c3]);
            ostream.write(map[c4]) ;
            count += 4 ;
            break ;
          }
          off += 3 ;
        }
        for ( int i = 0 ; i < 3 ;i++) 
          buffer[i] = (i < got-off) ? buffer[off+i] : ((byte) 0) ;
        off = got-off ;
	    } else {
        off += got;
	    }
    }
    
    switch (off) {
	  case 1:
      ostream.write(map[get1(buffer, 0)]) ;
      ostream.write(map[get2(buffer, 0)]) ;
      ostream.write('=') ;
      ostream.write('=') ;
      break ;
	  case 2:
      ostream.write(map[get1(buffer, 0)]);
      ostream.write(map[get2(buffer, 0)]);
      ostream.write(map[get3(buffer, 0)]);
      ostream.write('=');
    }
    ostream.flush();
  }
  
  private static final int get1(byte buf[], int off) {
    return (buf[off] & 0xfc) >> 2 ;
  }

  private static final int get2(byte buf[], int off) {
    return ((buf[off]&0x3) << 4) | ((buf[off+1]&0xf0) >>> 4) ;
  }

  private static final int get3(byte buf[], int off) {
    return ((buf[off+1] & 0x0f) << 2) | ((buf[off+2] & 0xc0) >>> 6) ;
  }

  private static final int get4(byte buf[], int off) {
    return buf[off+2] & 0x3f ;
  }
  
}
