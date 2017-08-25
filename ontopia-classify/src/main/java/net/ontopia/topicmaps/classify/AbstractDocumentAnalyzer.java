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

/**
 * INTERNAL: 
 */
public abstract class AbstractDocumentAnalyzer implements DocumentAnalyzerIF {

  protected int iterations = 1;
  protected int iteration;
  
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
    // no-op
  }
  
  public abstract void analyzeToken(TextBlock parent, Token token, int index);

  public void endRegion(Region region) {
    // no-op
  }

  public void endDocument(Document doc) {
    // no-op
  }

  public void endAnalysis() {
    // no-op
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
