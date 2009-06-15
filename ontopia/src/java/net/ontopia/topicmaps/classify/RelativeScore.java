
// $Id: RelativeScore.java,v 1.5 2007/07/13 06:21:21 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class RelativeScore implements TermAnalyzerIF {

  TermDatabase tdb;
  double maxScore;
  
  public void analyzeTerm(Term term) {
    term.divideScore(maxScore, "relative adjustment");
  }
  
  public void startAnalysis(TermDatabase tdb) {
    this.tdb = tdb;
    this.maxScore = tdb.getMaxScore();
  }

  public void endAnalysis() {
    this.tdb = null;
  }
  
}
