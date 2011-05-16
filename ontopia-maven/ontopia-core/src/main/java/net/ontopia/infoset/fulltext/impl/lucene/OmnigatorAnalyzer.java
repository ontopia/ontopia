
// $Id: OmnigatorAnalyzer.java,v 1.4 2005/07/08 10:45:25 grove Exp $

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;
  
/**
 * INTERNAL: A Lucene analyzer implementation used by the Omnigator
 * plugins. The analyzer breaks up tokens according to the
 * Character.isLetterOrDigit(char) method and also lower-cases
 * characters, so that search can be case-insensitive.<p>
 *
 * @since 2.1.1
 */

public class OmnigatorAnalyzer extends Analyzer {

  public static final OmnigatorAnalyzer INSTANCE = new OmnigatorAnalyzer();

  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new OmnigatorTokenizer(reader);
  }
  
  private static class OmnigatorTokenizer extends CharTokenizer {
    
    OmnigatorTokenizer(Reader in) {
      super(in);
    }
    
    protected boolean isTokenChar(char c) {
      return Character.isLetterOrDigit(c);
    }
    
    protected char normalize(char c) {
      return Character.toLowerCase(c);
    }
    
  }

}
