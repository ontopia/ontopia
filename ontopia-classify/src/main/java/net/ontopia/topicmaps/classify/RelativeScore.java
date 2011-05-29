
package net.ontopia.topicmaps.classify;

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
