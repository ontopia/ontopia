
// $Id: PDFFormatModule.java,v 1.7 2007/05/07 08:11:39 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.xml.*;
import net.ontopia.utils.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * INTERNAL: 
 */
public class PDFFormatModule implements FormatModuleIF {
  protected String[] extensions = new String[] {".pdf"};
  protected byte[] magicBytes = FormatModule.getBytes("%PDF-");
  
  public boolean matchesContent(ClassifiableContentIF cc) {
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return FormatModule.matchesExtension(cc.getIdentifier(), extensions);
  }

  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    try {
      PDDocument pdoc = PDDocument.load(new BufferedInputStream(new ByteArrayInputStream(cc.getContent())));
      PDFTextStripper stripper = new PDFTextStripper();
      String s = stripper.getText(pdoc);
      pdoc.close();
      char[] c = s.toCharArray();
      handler.startRegion("document");
      handler.text(c, 0, c.length);
      handler.endRegion();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }    
  }
  
}
