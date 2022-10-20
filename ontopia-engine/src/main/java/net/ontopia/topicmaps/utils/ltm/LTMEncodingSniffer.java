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

package net.ontopia.topicmaps.utils.ltm;

import java.io.IOException;
import java.io.PushbackInputStream;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.impl.utils.EncodingSnifferIF;

/**
 * INTERNAL: An encoding sniffer for LTM.
 */
public class LTMEncodingSniffer implements EncodingSnifferIF {
  
  @Override
  public String guessEncoding(PushbackInputStream stream) throws IOException {
    String encoding;

    // Look to seee if there's a UTF-8 BOM (Byte Order Mark) at the
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
      
      if (!foundBom) {
        stream.unread(bomBuffer, 0, 3);
      }
    } 
    if (foundBom) { 
      encoding = "utf-8";
    } else {
      encoding = "iso-8859-1";
    }

    // Now look for an encoding declaration
    byte[] buf = new byte[50];
    int read = stream.read(buf, 0, 50);
    if (read != -1) {
      String start = new String(buf);
      stream.unread(buf, 0, read);
      
      // Get the encoding (if any) declared in the document.
      if (start.startsWith("@\"")) {
        int end = start.indexOf('"', 2);
        if (end != -1) {
          encoding = start.substring(2, end);
        }
      }
      
      // If a BOM is found then the encoding must be utf-8.
      if (foundBom && encoding != null && !encoding.equals("utf-8")) {
        throw new OntopiaRuntimeException("Contradicting encoding information."
            + " The BOM indicates that the encoding should be utf-8,"
            + " but the encoding is declared to be: " + encoding + ".");
      }

      return encoding;
    }
    return encoding;
  }  
}
