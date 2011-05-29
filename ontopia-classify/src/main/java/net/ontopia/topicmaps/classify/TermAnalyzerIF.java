
package net.ontopia.topicmaps.classify;

/**
 * INTERNAL: 
 */
public interface TermAnalyzerIF {
  
  public void startAnalysis(TermDatabase tdb);
  
  public void analyzeTerm(Term term);

  public void endAnalysis();
  
}
