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
package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.PushbackInputStream;

import net.ontopia.topicmaps.impl.utils.EncodingSnifferIF;

/**
 * INTERNAL: An encoding sniffer for JTM.
 */
public class JTMEncodingSniffer implements EncodingSnifferIF {
  
  @Override
  public String guessEncoding(PushbackInputStream stream) throws IOException {
    // http://www.ietf.org/rfc/rfc4627.txt requires that a JSON data stream
    // has to be in UTF notation. This code checks which specific UTF format
    // is being used.

    String encoding = "UTF8";
    
    byte[] buffer = new byte[4];
    int bytesread = stream.read(buffer, 0, 4);    
    if (bytesread == 4) {
      // check for the different UTF formats:
      //
      // 00 00 00 xx  UTF-32BE
      // 00 xx 00 xx  UTF-16BE
      // xx 00 00 00  UTF-32LE
      // xx 00 xx 00  UTF-16LE
      // xx xx xx xx  UTF-8

      // Note: UTF32 is not supported by Java
      // http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html
      
      if (buffer[0] == (byte) 0x00 &&
          buffer[1] != (byte) 0x00 &&
          buffer[2] == (byte) 0x00 &&
          buffer[3] != (byte) 0x00) {
        encoding = "UnicodeBigUnmarked";
      } else if (buffer[0] != (byte) 0x00 &&
          buffer[1] == (byte) 0x00 &&
          buffer[2] != (byte) 0x00 &&
          buffer[3] == (byte) 0x00) {
        encoding = "UnicodeLittleUnmarked";
      }
    } 

    stream.unread(buffer, 0, bytesread);
    return encoding;
  }  
}
