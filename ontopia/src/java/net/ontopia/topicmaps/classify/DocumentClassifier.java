
// $Id: DocumentClassifier.java,v 1.26 2007/03/12 08:40:46 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class DocumentClassifier {

  TermDatabase tdb;

  TermStemmerIF termStemmer;

  List docAnalyzers = new ArrayList();
  List termAnalyzers = new ArrayList();

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
    List children = region.getChildren();
    int size = children.size();
    for (int i=0; i < size; i++) {
      Object child = children.get(i);
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

    List tokens = tb.getTokens();
    for (int i=0; i < tokens.size(); i++) {
      Token token = (Token)tokens.get(i);
      
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
      int size = docAnalyzers.size();
      for (int i=0; i < size; i++) {
        DocumentAnalyzerIF analyzer = (DocumentAnalyzerIF)docAnalyzers.get(i);
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
    List children = region.getChildren();
    int size = children.size();
    for (int i=0; i < size; i++) {
      Object child = children.get(i);
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
    List tokens = tb.getTokens();
    int size = tokens.size();
    for (int i=0; i < size; i++) {
      Token t = (Token)tokens.get(i);
      analyzer.analyzeToken(tb, t, i);
    }
  }

  // --------------------------------------------------------------------------
  // term analysis
  // --------------------------------------------------------------------------
  
  public void analyzeTerms() {
    if (termAnalyzers != null && !termAnalyzers.isEmpty()) {
      int size = termAnalyzers.size();
      for (int i=0; i < size; i++) {
        TermAnalyzerIF analyzer = (TermAnalyzerIF)termAnalyzers.get(i);
        analyzer.startAnalysis(tdb);
        try {
          Object[] terms = tdb.getTerms().toArray(); // create array to avoid CME
          for (int x=0; x < terms.length; x++) {
            analyzer.analyzeTerm((Term)terms[x]);
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
    Iterator iter = termAnalyzers.iterator();
    while (iter.hasNext()) {
      Object ta = iter.next();
      if (ta instanceof CompoundAnalyzer) {
        CompoundAnalyzer ca = (CompoundAnalyzer)ta;
        Object[] terms = tdb.getTermsByRank();
        for (int i=0; i < terms.length; i++) {
          ca.dump((Term)terms[i]);
        }
      }
    }
  }
  
}
