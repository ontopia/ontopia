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
public class DocumentClassifier {
  private TermDatabase tdb;
  private TermStemmerIF termStemmer;
  private List<DocumentAnalyzerIF> docAnalyzers = new ArrayList<DocumentAnalyzerIF>();
  private List<TermAnalyzerIF> termAnalyzers = new ArrayList<TermAnalyzerIF>();

  public DocumentClassifier(TermDatabase tdb) {
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
  
  public void setTermStemmer(TermStemmerIF stemmer) {
    this.termStemmer = stemmer;
  }
  
  public void addDocumentAnalyzer(DocumentAnalyzerIF analyzer) {
    this.docAnalyzers.add(analyzer);
  }

  public void addTermAnalyzer(TermAnalyzerIF analyzer) {
    this.termAnalyzers.add(analyzer);
  }
  
  // --------------------------------------------------------------------------
  // term extraction
  // --------------------------------------------------------------------------

  protected void extractTerms(Document doc) {
    // turn text blocks into lists of terms
    extractTerms(doc.getRoot());
  }
  
  protected void extractTerms(Region region) {
    // loop over region's children
    for (Object child : region.getChildren()) {
      if (child instanceof TextBlock) {
        TextBlock tb = (TextBlock)child;
        extractTerms(region, tb);
      } else {
        Region tr = (Region)child;
        extractTerms(tr);
      }
    }
  }
  
  protected void extractTerms(Region parent, TextBlock tb) {
    for (Token token : tb.getTokens()) {
      if (token.getType() == Token.TYPE_VARIANT) {
        Variant variant = (Variant)token;
        Term term = variant.getTerm();
        if (term == null) {        
          String normalized = token.getValue();        
          String stem = termStemmer.stem(normalized);
          term = tdb.createTerm(stem);
          variant.setTerm(term);
        }
        term.addVariant(variant);
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // document analysis
  // --------------------------------------------------------------------------
  
  public void analyzeDocument(Document doc) {
    // turn text blocks into lists of terms
    extractTerms(doc);
    // do document analysis
    if (docAnalyzers != null && !docAnalyzers.isEmpty()) {
      Region root = doc.getRoot();
      for (DocumentAnalyzerIF analyzer : docAnalyzers) {
        analyzer.startAnalysis();
        try {
          while (analyzer.doDocumentAnalysis()) {
            analyzer.startDocument(doc);
            analyzeRegion(root, analyzer);
            analyzer.endDocument(doc);
          }
        } finally {
          analyzer.endAnalysis();
        }
      }
    }
  }
  
  protected void analyzeRegion(Region region, DocumentAnalyzerIF analyzer) {
    analyzer.startRegion(region);
    // loop over region's children
    for (Object child : region.getChildren()) {
      if (child instanceof TextBlock) {
        TextBlock tb = (TextBlock)child;
        analyzeTextBlock(region, tb, analyzer);
      } else {
        Region tr = (Region)child;
        analyzeRegion(tr, analyzer);
      }
    }
    analyzer.endRegion(region);
  }
  
  protected void analyzeTextBlock(Region parent, TextBlock tb, DocumentAnalyzerIF analyzer) {
    // loop over terms in text block
    List<Token> tokens = tb.getTokens();
    int size = tokens.size();
    for (int i=0; i < size; i++) {
      Token t = tokens.get(i);
      analyzer.analyzeToken(tb, t, i);
    }
  }

  // --------------------------------------------------------------------------
  // term analysis
  // --------------------------------------------------------------------------
  
  public void analyzeTerms() {
    if (termAnalyzers != null && !termAnalyzers.isEmpty()) {
      for (TermAnalyzerIF analyzer : termAnalyzers) {
        analyzer.startAnalysis(tdb);
        try {
          Term[] terms = tdb.getTerms().toArray(new Term[] {}); // create array to avoid CME
          for (int x=0; x < terms.length; x++) {
            analyzer.analyzeTerm(terms[x]);
          }

        } finally {
          analyzer.endAnalysis();
        }
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // debug
  // --------------------------------------------------------------------------

  public void dump() {
    for (TermAnalyzerIF ta : termAnalyzers) {
      if (ta instanceof CompoundAnalyzer) {
        CompoundAnalyzer ca = (CompoundAnalyzer)ta;
        Term[] terms = tdb.getTermsByRank();
        for (int i=0; i < terms.length; i++) {
          ca.dump(terms[i]);
        }
      }
    }
  }  
}
