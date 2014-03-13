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

package net.ontopia.net.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import net.ontopia.net.Base64Decoder;
import net.ontopia.net.Base64Encoder;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A class for parsing and representing data URLs.
 */

public class DataURL {
  
  private byte[]  contents;
  private int     length;
  private String  mediaType;
  private boolean base64;
  
  public DataURL(URL url) throws IOException {
    this(url.toExternalForm());
  }
  
  public DataURL(String rawurl) throws IOException {
    // cut off "data:"
    rawurl = rawurl.substring(5);

    // parse the URL parameters
    int semiPos = rawurl.indexOf(';');
    int commaPos = rawurl.indexOf(',');

    if (commaPos == -1)
      throw new IOException("data URL syntax error: comma required");
    
    mediaType = "text/plain";
    base64   = false;

    // data:" [ mediatype ] [ ";base64" ] "," data
    
    if (commaPos > 0) {
      
      // parse media type
      if (semiPos > 0) 
        mediaType = rawurl.substring(0, semiPos);
      else if (semiPos == -1)
        mediaType = rawurl.substring(0, commaPos);
        
      // FIXME: should really parse out mediatype param values here
      // 	while (semiPos != -1 && semiPos < commaPos) {
      // 	  int next = rawurl.indexOf(';', semiPos + 1);	 
      // 	}

      // check content encoding
      base64 = rawurl.substring(commaPos - 7, commaPos).equals(";base64");
    }

    if (base64)
      decodeBase64(rawurl, commaPos + 1);    
    else
      decodeURL(rawurl, commaPos + 1);
  }

  public int getContentLength() {
    return length;
  }

  public String getMediaType() {
    return mediaType;
  }

  public String getContentEncoding() {
    if (base64)
      return "base64";
    else
      return null;
  }
  
  public String getContents() {
    try {
      return new String(contents, 0, length, "iso-8859-1");
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e); // shouldn't be possible
    }
  }
  
  public String getEncodedContents() {
    if (base64) {
      try {
        return Base64Encoder.encode(new String(contents, 0, length));
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else {
      return getContents();
    }
  }
  
  public ByteArrayInputStream getContentsAsStream() throws IOException {
    return new ByteArrayInputStream(contents, 0, length);
  }

  // --- Internal methods

  // decode a URL from base64 encoding to the raw bytes encoded by the
  // URL string, as described in section 6.8 of RFC 2045
  
  private void decodeBase64(String rawurl, int start) throws IOException {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    Base64Decoder.decode(rawurl.substring(start, rawurl.length()), ostream);
    contents = ostream.toByteArray();
    length = contents.length;
  }
  
  private void decodeURL(String rawurl, int start) throws IOException {
    char[] rawsource = rawurl.toCharArray();
    byte[] decoded   = new byte[rawurl.length()];

    int written = 0;
    for (int ix = start; ix < rawsource.length; ix++) {
      if (rawsource[ix] == '%') {
        if (ix + 2 >= rawsource.length) break; // FIXME
        decoded[written++] = (byte) ((digitVal(rawsource[ix + 1]) * 16) +
                                     digitVal(rawsource[ix + 2]));
        ix += 2;
      } else if (rawsource[ix] == '+')
        decoded[written++] = ' ';
      else
        decoded[written++] = (byte) rawsource[ix];
    }
    
    // set up internal members
    contents = decoded;
    length = written;
  }
  
  private byte digitVal(char hexDigit) throws IOException {
    if (hexDigit >= '0' && hexDigit <= '9')
      return (byte) (hexDigit - '0');
    else if (hexDigit >= 'A' && hexDigit <= 'F')
      return (byte) (hexDigit - '7');  // 'A' - 10 = '7'
    else if (hexDigit >= 'a' && hexDigit <= 'f')
      return (byte) (hexDigit - 'W');  // 'a' - 10 = 'W'
    else
      throw new IOException("data URL syntax error: Invalid hex digit");
  }
  
  public String toExternalForm() {
    StringBuilder sb = new StringBuilder();
    sb.append("data:");
    if (mediaType != null && !mediaType.equals("text/plain"))
      sb.append(mediaType);
    if (base64) sb.append(";base64");
    sb.append(",");
    sb.append(URLEncoder.encode(getEncodedContents()));
    return sb.toString();
  }

  public String toString() {
    return toExternalForm();
  }
  
}
