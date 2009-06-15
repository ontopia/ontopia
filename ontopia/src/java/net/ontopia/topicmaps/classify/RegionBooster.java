
// $Id: RegionBooster.java,v 1.3 2007/07/13 06:21:21 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class RegionBooster extends AbstractDocumentAnalyzer {

  Map regions;
  double boost = 1.0d;

  public RegionBooster() {
    super(1);
    this.regions = new HashMap();
  }

  public void addBoost(String rname, double boost) {
    regions.put(rname, new Double(boost));
  }
  
  public void startRegion(Region region) {
    super.startRegion(region);
    String rname = region.getName();
    Double d = (Double)regions.get(rname);    
    if (d != null) {
      this.boost = d.doubleValue();
    } else {
      this.boost = 1.0d;
    }      
  }
  
  public void analyzeToken(TextBlock parent, Token token, int index) {
    // ignore non variant tokens
    if (token.getType() != Token.TYPE_VARIANT) return;
    
    Term term = ((Variant)token).getTerm();
    double score = term.getScore();
    if (score > 0d)
      term.multiplyScore(boost, "region boost");
  }

  public void endRegion(Region region) {
    super.endRegion(region);
  }
  
}
