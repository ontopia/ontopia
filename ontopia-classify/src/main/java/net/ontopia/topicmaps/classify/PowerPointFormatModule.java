
package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.xml.*;
import net.ontopia.utils.*;

import org.apache.poi.hslf.extractor.PowerPointExtractor;

/**
 * INTERNAL: 
 */
public class PowerPointFormatModule implements FormatModuleIF {

  protected String[] extensions = new String[] {".ppt", ".pot", ".pps"};
  protected byte[] magicBytes = new byte[] {(byte)0xd0, (byte)0xcf, (byte)0x11, (byte)0xe0, (byte)0xa1, (byte)0xb1, (byte)0x1a, (byte)0xe1, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
  
  public boolean matchesContent(ClassifiableContentIF cc) {
    return false;
  }

  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    boolean matches = FormatModule.matchesExtension(cc.getIdentifier(), extensions);
    if (!matches) return false;
    // name matches, then check office magic bytes
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    try {
      PowerPointExtractor extractor = new PowerPointExtractor(new BufferedInputStream(new ByteArrayInputStream(cc.getContent())));
      String s = extractor.getText();
      char[] c = s.toCharArray();
      handler.startRegion("document");
      handler.text(c, 0, c.length);
      handler.endRegion();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }    
  }
  
}
