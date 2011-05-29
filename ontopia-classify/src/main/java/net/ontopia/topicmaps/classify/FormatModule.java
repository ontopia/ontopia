
package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.*;

import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public class FormatModule implements FormatModuleIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(FormatModule.class.getName());
  
  protected List modules;
  protected FormatModuleIF fallout_module;
  
  public FormatModule() {
    modules = new ArrayList();
    modules.add(new XMLFormatModule());
    modules.add(new HTMLFormatModule());
    modules.add(new PDFFormatModule());
    modules.add(new WordFormatModule());
    modules.add(new PowerPointFormatModule());
    modules.add(new OOXMLWordFormatModule());
    modules.add(new OOXMLPowerpointFormatModule());
    fallout_module = new PlainTextFormatModule();
    modules.add(fallout_module);
  }
  
  public boolean matchesContent(ClassifiableContentIF cc) {
    return true;
  }

  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return true;
  }
  
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    // detect document format
    FormatModuleIF fm = detectFormat(cc);
    // read document
    fm.readContent(cc, handler);
  }
  
  public FormatModuleIF detectFormat(ClassifiableContentIF cc) {

    // auto-detect by looking at document content
    for (int i=0; i < modules.size(); i++) {
      FormatModuleIF fm = (FormatModuleIF)modules.get(i);
      if (fm.matchesContent(cc)) {
        log.debug("Match content: " + cc.getIdentifier() + ", format: " + fm);
        return fm;
      }
    }

    // auto-detect by looking at document identifier
    for (int i=0; i < modules.size(); i++) {
      FormatModuleIF fm = (FormatModuleIF)modules.get(i);
      if (fm.matchesIdentifier(cc)) {
        log.debug("Match uri: " + cc.getIdentifier() + ", format: " + fm);
        return fm;
      }
    }
    
    return fallout_module;
  }
  
  // --------------------------------------------------------------------------
  // extension matching
  // --------------------------------------------------------------------------

  public static boolean matchesExtension(String uri, String[] extensions) {
    if (extensions == null) return false;
    String luri = uri.toLowerCase();
    for (int i=0; i < extensions.length; i++) {
      if (luri.endsWith(extensions[i])) return true;
    }
    return false;
  }

  // --------------------------------------------------------------------------
  // content matching
  // --------------------------------------------------------------------------

  private static byte[][] boms = new byte[][] {
    new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF }, // UTF-8
    new byte[] { (byte)0xFE, (byte)0xFF }, // UTF-16 Big Endian
    new byte[] { (byte)0xFF, (byte)0xFE }, // UTF-16 Little Endian
    new byte[] { (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0xFF }, // UTF-32 Big Endian
    new byte[] { (byte)0xFF, (byte)0xFE, (byte)0x00, (byte)0x00 } // UTF-32 Little Endian
  };
  
  private static String[] bomnames = new String[] {
    "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-32BE", "UTF-32LE"
  };

  public static String getCharSetName(int charSet) {
    return bomnames[charSet];
  }

  public static int getOffset(int charSet) {
    return boms[charSet].length-1;
  }
  
  public static int detectCharSet(byte[] content) {
    // check byte order mark
    for (int i=0; i < boms.length; i++) {
      byte[] bom = boms[i];
      if (startsWith(content, bom))
        return i;
    }
    return -1;
  }

  public static byte[] getBytes(String s) {
    try {
      return s.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public static byte[][] getBytes(String[] s) {
    try {
      byte[][] b = new byte[s.length][];
      for (int i=0; i < s.length; i++) {
        b[i] = s[i].getBytes("UTF-8");
      }
      return b;
    } catch (UnsupportedEncodingException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public static boolean startsWith(byte[] content, byte[] s) {
    if (content == null || content.length < s.length) return false;
    for (int i=0; i < s.length; i++) {
      if (content[i] != s[i]) return false;
    }
    return true;
  }

  public static boolean startsWithSkipWhitespace(byte[] content, byte[][] ss) {
    int offset = getLeadingWhitespace(content);
    outer:
    for (int i=0; i < ss.length; i++) {
      byte[] s = ss[i];
      for (int o=offset; o < s.length; o++) {
        if (content[o+offset] != s[o]) continue outer;
      }
      return true;
    }
    return false;
  }

  public static boolean startsWithSkipWhitespace(byte[] content, byte[] s) {
    int offset = getLeadingWhitespace(content);
    for (int i=0; i < s.length; i++) {
      if (content[i+offset] != s[i]) return false;
    }
    return true;
  }

  private static int getLeadingWhitespace(byte[] content) {
    // skip leading white space
    int offset = 0;
    for (int i=0; i < content.length; i++) {
      char c = (char)content[i];
      if (Character.isWhitespace(c) || c == '\u0000' || c == '\u00ff' || c == '\u00fe' || c == '\u00ef' || c == '\u00ef' || c == '\u00bb' || c == '\u00bf')
        offset++;
      else
        break;
    }
    return offset;
  }
  
}
