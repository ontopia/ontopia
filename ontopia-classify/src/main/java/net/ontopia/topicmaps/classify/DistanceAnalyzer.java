
package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class DistanceAnalyzer extends AbstractDocumentAnalyzer {

  protected double high;
  protected int termCount;
  
  public DistanceAnalyzer() {
    super(2);
  }
  
  public void startDocument(Document doc) {
    super.startDocument(doc);
    if (this.iteration == 1)
      this.high = 0d;
    else
      this.termCount = 0;
  }
  
  public void endDocument(Document doc) {
    // calculate high
    if (this.iteration == 1)
      this.high = Math.log(termCount);
  }

  public void analyzeToken(TextBlock parent, Token token, int index) {
    // ignore non variant tokens
    if (token.getType() != Token.TYPE_VARIANT) return;
    
    // count term
    this.termCount++;

    // adjust score by distance from start
    if (this.iteration > 1) {
      Term term = ((Variant)token).getTerm();
      double score = term.getScore();
      // if no existing score add distance score
      if (score > 0d)
        term.addScore(high - Math.log(termCount), "distance adjustment");
    }
  }
  
}
