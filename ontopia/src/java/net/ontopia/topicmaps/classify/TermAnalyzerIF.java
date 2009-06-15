
// $Id: TermAnalyzerIF.java,v 1.4 2006/11/23 09:07:17 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public interface TermAnalyzerIF {
  
  public void startAnalysis(TermDatabase tdb);
  
  public void analyzeTerm(Term term);

  public void endAnalysis();
  
}
