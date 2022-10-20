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
public class DistanceAnalyzer extends AbstractDocumentAnalyzer {

  protected double high;
  protected int termCount;
  
  public DistanceAnalyzer() {
    super(2);
  }
  
  @Override
  public void startDocument(Document doc) {
    super.startDocument(doc);
    if (this.iteration == 1) {
      this.high = 0d;
    } else {
      this.termCount = 0;
    }
  }
  
  @Override
  public void endDocument(Document doc) {
    // calculate high
    if (this.iteration == 1) {
      this.high = Math.log(termCount);
    }
  }

  @Override
  public void analyzeToken(TextBlock parent, Token token, int index) {
    // ignore non variant tokens
    if (token.getType() != Token.TYPE_VARIANT) {
      return;
    }
    
    // count term
    this.termCount++;

    // adjust score by distance from start
    if (this.iteration > 1) {
      Term term = ((Variant)token).getTerm();
      double score = term.getScore();
      // if no existing score add distance score
      if (score > 0d) {
        term.addScore(high - Math.log(termCount), "distance adjustment");
      }
    }
  }
  
}
