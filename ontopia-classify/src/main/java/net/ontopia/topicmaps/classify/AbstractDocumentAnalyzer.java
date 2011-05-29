
package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public abstract class AbstractDocumentAnalyzer implements DocumentAnalyzerIF {

  protected int iterations = 1;
  protected int iteration;
  
  public AbstractDocumentAnalyzer() {
  }
  
  public AbstractDocumentAnalyzer(int iterations) {
    this.iterations = iterations;
  }

  // --------------------------------------------------------------------------
  // iterator
  // --------------------------------------------------------------------------
  
  public boolean doDocumentAnalysis() {
    return (iteration < iterations);
  }

  // --------------------------------------------------------------------------
  // events
  // --------------------------------------------------------------------------

  public void startAnalysis() {
    this.iteration = 0;
  }

  public void startDocument(Document doc) {
    this.iteration++;
  }

  public void startRegion(Region region) {
  }
  
  public abstract void analyzeToken(TextBlock parent, Token token, int index);

  public void endRegion(Region region) {
  }

  public void endDocument(Document doc) {
  }

  public void endAnalysis() {
  }

  // --------------------------------------------------------------------------
  // utility methods
  // --------------------------------------------------------------------------

  //! protected Term getPreviousTerm(TextBlock parent, int index) {
  //!   List terms = parent.getTerms();
  //!   for (int i = index - 1; i >= 0; i--) {
  //!     Object c = terms.get(i);
  //!     if (c instanceof Term)
  //!       return (Term)c;
  //!   }
  //!   return null;
  //! }
  //! 
  //! protected Term getNextTerm(TextBlock parent, int index) {
  //!   List terms = parent.getTerms();
  //!   int size = terms.size();
  //!   for (int i = index + 1; i < size; i++) {
  //!     Object c = terms.get(i);
  //!     if (c instanceof Term)
  //!       return (Term)c;
  //!   }
  //!   return null;
  //! }
  
}
