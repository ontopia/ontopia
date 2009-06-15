
// $Id: PlainTextFormatModule.java,v 1.7 2007/05/07 08:11:39 geir.gronmo Exp $

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
