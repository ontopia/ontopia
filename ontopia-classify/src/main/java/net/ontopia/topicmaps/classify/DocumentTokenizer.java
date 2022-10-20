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

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL: 
 */
public class DocumentTokenizer {
  private TermDatabase tdb;
  private TokenizerIF tokenizer;
  private DelimiterTrimmerIF delimiterTrimmer;
  private List<TermNormalizerIF> termNormalizers = new ArrayList<TermNormalizerIF>();

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
    for (Object child : region.getChildren()) {
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
    if (token == null) {
      return;
    }
    
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
        TermNormalizerIF normalizer = termNormalizers.get(i);
        normalized = normalizer.normalize(normalized);
        if (normalized == null) {
          break;
        }
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
    if (delimiterBefore != null) {
      tb.addToken(tdb.createDelimiter(delimiterBefore));
    }

    // add token to text block
    tb.addToken(t);
    
    // add after delimiter
    if (delimiterAfter != null) {
      tb.addToken(tdb.createDelimiter(delimiterAfter));
    }
  }
  
}
