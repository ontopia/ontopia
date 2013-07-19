/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.xml.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class PlainTextFormatModule implements FormatModuleIF {

  protected String[] extensions = new String[] {".txt"};
  
  public boolean matchesContent(ClassifiableContentIF cc) {
    return false;
  }

  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return FormatModule.matchesExtension(cc.getIdentifier(), extensions);
  }

  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    try {
      // try to detect character set
      int charSetId = FormatModule.detectCharSet(cc.getContent());
      handler.startRegion("document");
      if (charSetId >= 0) {
        String charSet = FormatModule.getCharSetName(charSetId);
        int offset = FormatModule.getOffset(charSetId);
        String s = new String(cc.getContent(), charSet);
        char[] c = s.toCharArray();
        handler.text(c, offset, c.length-offset);
      } else {
        // use default character set
        String s = new String(cc.getContent());
        char[] c = s.toCharArray();
        handler.text(c, 0, c.length);
      }
      handler.endRegion();
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

}
