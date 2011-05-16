
// $Id: TermAnalyzerIF.java,v 1.4 2006/11/23 09:07:17 grove Exp $

package net.ontopia.topicmaps.classify;

/**
 * INTERNAL: 
 */
public interface TermAnalyzerIF {
  
  public void startAnalysis(TermDatabase tdb);
  
  public void analyzeTerm(Term term);

  public void endAnalysis();
  
}
