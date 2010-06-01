
// $Id: DocumentTokenizer.java,v 1.3 2007/03/14 14:01:38 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
  
/**
 * INTERNAL: 
 */
public class DocumentTokenizer {
  TermDatabase tdb;
  TokenizerIF tokenizer;
  DelimiterTrimmerIF delimiterTrimmer;
  List termNormalizers = new ArrayList();

  public DocumentTokenizer(TermDatabase tdb) {
    this.tdb = tdb;
  }

  public TermDatabase getTermDatabase() {
    return tdb;
  }

  public void setTermDatabase(TermDatabase tdb) {    
    this.tdb = tdb;
  }
  
  // --------------------------------------------------------------------------
  // configuration
  // --------------------------------------------------------------------------

  public void setTokenizer(TokenizerIF tokenizer) {
    this.tokenizer = tokenizer;
  }

  public void setDelimiterTrimmer(DelimiterTrimmerIF trimmer) {
    this.delimiterTrimmer = trimmer;
  }
  
  public void addTermNormalizer(TermNormalizerIF normalizer) {
    this.termNormalizers.add(normalizer);
  }
  
  // --------------------------------------------------------------------------
  // document tokenization
  // --------------------------------------------------------------------------
  
  public void tokenize(Document doc) {
    // turn text blocks into lists of tokens
    tokenize(doc.getRoot());
    doc.setTokenized(true);
  }
  
  protected void tokenize(Region region) {
    // loop over region's children
    List children = region.getChildren();
    int size = children.size();
    for (int i=0; i < size; i++) {
      Object child = children.get(i);
      if (child instanceof TextBlock) {
        TextBlock tb = (TextBlock)child;
        tokenize(region, tb);
      } else {
        Region tr = (Region)child;
        tokenize(tr);
      }
    }
  }
  
  protected void tokenize(Region parent, TextBlock tb) {
    String text = tb.getText();
    
    // tokenize
    tokenizer.setText(text);
    while (tokenizer.next()) {
      // normalize term (stemming, junk filter, synonyms etc.)
      tokenize(tb, tokenizer.getToken());
    }
  }
  
  protected void tokenize(TextBlock tb, String token) {
    if (token == null) return;
    
    // sentence boundaries; extract delimiters
    String delimiterBefore = null;
    String delimiterAfter = null;
    int six = delimiterTrimmer.trimStart(token);
    int eix = delimiterTrimmer.trimEnd(token);
    if (six > 0 && eix > six && eix < token.length() - 1) {
      delimiterBefore = token.substring(0, six);
      delimiterAfter = token.substring(eix+1);
      token = token.substring(six, eix+1);
    } else if (six > 0) {
      delimiterBefore = token.substring(0, six);
      token = token.substring(six);
    } else if (eix < token.length() - 1) {
      delimiterAfter = token.substring(eix+1);
      token = token.substring(0, eix+1);
    }
    
    // normalize token
    String normalized = token;
    if (termNormalizers != null && !termNormalizers.isEmpty()) {
      int size = termNormalizers.size();
      for (int i=0; i < size; i++) {
        TermNormalizerIF normalizer = (TermNormalizerIF)termNormalizers.get(i);
        normalized = normalizer.normalize(normalized);
        if (normalized == null) break;
      }
    }

    // create token object
    Token t;
    if (normalized == null) {
      // found junk
      t = tdb.createDelimiter(normalized);
    } else {
      // found variant
      t = tdb.createVariant(normalized);
    }

    // add before delimiter
    if (delimiterBefore != null)
      tb.addToken(tdb.createDelimiter(delimiterBefore));

    // add token to text block
    tb.addToken(t);
    
    // add after delimiter
    if (delimiterAfter != null)
      tb.addToken(tdb.createDelimiter(delimiterAfter));
  }
  
}
