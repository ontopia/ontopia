
package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public interface DocumentAnalyzerIF {
  
  public boolean doDocumentAnalysis();
  
  public void startAnalysis();
  
  public void startDocument(Document doc);

  public void startRegion(Region region);
  
  public void analyzeToken(TextBlock parent, Token token, int index);

  public void endRegion(Region region);

  public void endDocument(Document doc);

  public void endAnalysis();
  
}
